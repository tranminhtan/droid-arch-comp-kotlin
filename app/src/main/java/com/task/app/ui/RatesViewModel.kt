package com.task.app.ui

import androidx.annotation.DrawableRes
import androidx.collection.ArrayMap
import com.task.app.base.DataBindingRecyclerViewAdapter
import com.task.app.base.ResourcesProvider
import com.task.app.base.SchedulersProvider
import com.task.app.service.CurrencyRateRepository
import com.task.app.service.CurrencyRateRepository.Companion.EMPTY_RATES_ITEM
import com.task.app.ui.list.RatesItem
import com.task.app.ui.support.CurrencyHelper
import com.task.app.ui.support.OnClickRatesItemObservable
import com.task.app.ui.support.OnTextWatcherObservable
import com.task.app.ui.support.isEqual
import com.task.app.ui.support.toBigDecimalOrZero
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import java.math.BigDecimal
import java.math.MathContext
import java.util.Collections
import java.util.Currency
import java.util.concurrent.TimeUnit

private const val DELAY_IN_SEC = 1L

class RatesViewModel(
    private val schedulersProvider: SchedulersProvider,
    private val repository: CurrencyRateRepository,
    private val resourcesProvider: ResourcesProvider,
    private val onClickRatesItemObservable: OnClickRatesItemObservable,
    private val onTextWatcherObservable: OnTextWatcherObservable,
    val adapter: DataBindingRecyclerViewAdapter<RatesItem>
) {

    init {
        adapter.setList(repository.getPlaceHolderRates())
    }

    // Trade off memory for better performance
    private val flagIconsCache = ArrayMap<String, Int>()
    private val displayNamesCache = ArrayMap<String, String>()

    // A valve to turn on/off updating currency rate
    private val valveSubject = BehaviorSubject.create<RatesItem>()

    fun observeRateTextChange(): Observable<List<RatesItem>> {
        return onTextWatcherObservable.observeRateChange()
            .doOnNext { Timber.w("Text changed %s", it) }
            .doOnNext { emitRatesItem(EMPTY_RATES_ITEM) } // Stop spamming server
            .subscribeOn(schedulersProvider.computation())
            .switchMapSingle { newBase: String ->
                val baseItem = adapter.getList()[0] // First item is base item

                // Call api if the current base is 0
                if (baseItem.rate.toBigDecimalOrZero().isEqual(BigDecimal.ZERO)
                    && newBase.toBigDecimalOrZero().isEqual(BigDecimal.ZERO)
                ) {
                    Single.just(emptyList<RatesItem>())
                        .doOnSuccess { emitRatesItem(baseItem.copy(rate = newBase)) }
                }
                // Calculate rates locally
                else {
                    Observable.fromIterable(adapter.getList())
                        .map { currItem: RatesItem ->
                            val newRate = calculateNewRate(newBase, baseItem.rate, currItem.rate)
                            currItem.copy(rate = newRate)
                        }
                        .collectInto(arrayListOf<RatesItem>(), { list, item -> list.add(item) })
                        .map {
                            it[0] = it[0].copy(rate = newBase)
                            it
                        }
                        .compose(adapter.asRxTransformer().forSingle())
                        .delay(DELAY_IN_SEC, TimeUnit.SECONDS, schedulersProvider.computation())
                        .doOnSuccess { emitRatesItem(baseItem.copy(rate = newBase)) }
                }
            }
    }

    fun observeOnItemClick(): Observable<Any> {
        return onClickRatesItemObservable.observeClickItem()
            .doOnNext { emitRatesItem(EMPTY_RATES_ITEM) } // Stop spamming server
            .observeOn(schedulersProvider.ui())
            .switchMapSingle { item: RatesItem ->
                adapter.moveSelectedItemToTop(item)
                    .flatMap {
                        if (it.isNotEmpty()) {
                            Single.just(it).compose(adapter.asRxTransformer().forSingle())
                        } else {
                            Single.just(it)
                        }
                    }
                    .doOnSuccess { emitRatesItem(item) }
            }
    }

    fun observeGetCurrencyRatesInterval(): Observable<List<RatesItem>> {
        return valveSubject
            .distinctUntilChanged()
            .subscribeOn(schedulersProvider.computation())
            .switchMap { item: RatesItem ->
                Observable.just(item)
                    .takeWhile { it != EMPTY_RATES_ITEM } // Not stop spamming server signal
                    .switchMapSingle { validItem: RatesItem ->
                        getCurrencyRates(validItem.code, validItem.rate)
                            .onErrorReturnItem(Collections.emptyList()) // Simply swallow error
                    }
                    .filter { it.isNotEmpty() }
                    .compose(adapter.asRxTransformer().forObservable())
                    .delay(DELAY_IN_SEC, TimeUnit.SECONDS, schedulersProvider.computation())
                    .repeat()
            }
    }

    private fun emitRatesItem(item: RatesItem) {
        valveSubject.onNext(item)
    }

    // Get currency rates and convert to RatesItem list
    private fun getCurrencyRates(baseCode: String, baseRate: String): Single<List<RatesItem>> {
        return repository.getCurrencyRates(baseCode)
            .flatMapObservable { Observable.fromIterable(it.entries) }
            .map { entry -> toRatesItem(entry, baseRate) }
            .collectInto(initListWithBaseItem(baseCode, baseRate), { list, item -> list.add(item) })
            .map { it }
    }

    private fun initListWithBaseItem(code: String, rate: String): MutableList<RatesItem> {
        return mutableListOf(toRatesItem(code, rate, true))
    }

    private fun toRatesItem(entry: Map.Entry<String, Double>, baseRate: String): RatesItem {
        return toRatesItem(entry.key, calculateRate(baseRate, entry.value))
    }

    private fun toRatesItem(code: String, rate: String, enabled: Boolean = false): RatesItem {
        return RatesItem(code, getDisplayName(code), rate, getFlagRes(code), enabled)
    }

    private fun calculateRate(baseRate: String, rate: Double): String {
        return CurrencyHelper.format(baseRate.toBigDecimalOrZero().multiply(BigDecimal(rate)))
    }

    private fun calculateNewRate(newBase: String, currBase: String, currRate: String): String {
        return if (newBase.toBigDecimalOrZero().isEqual(BigDecimal.ZERO)
            || currBase.toBigDecimalOrZero().isEqual(BigDecimal.ZERO)
            || currRate.toBigDecimalOrZero().isEqual(BigDecimal.ZERO)
        ) {
            ""
        } else {
            CurrencyHelper.format(
                newBase.toBigDecimalOrZero().multiply(currRate.toBigDecimalOrZero())
                    .divide(currBase.toBigDecimalOrZero(), MathContext.DECIMAL64)
            )
        }
    }

    @DrawableRes
    private fun getFlagRes(code: String): Int {
        var res = flagIconsCache[code]
        if (res == null) {
            res = resourcesProvider.getDrawableResId(code)
            flagIconsCache[code] = res
        }
        return res
    }

    private fun getDisplayName(code: String): String {
        var name = displayNamesCache[code]
        if (name == null) {
            name = Currency.getInstance(code).displayName
            displayNamesCache[code] = name
        }
        return name ?: ""
    }
}