package com.test.app.ui

import androidx.annotation.DrawableRes
import androidx.collection.ArrayMap
import com.test.app.base.ResourcesProvider
import com.test.app.service.CurrencyRateRepository
import com.test.app.service.CurrencyRateRepository.Companion.EMPTY_RATES_ITEM
import com.test.app.ui.list.CurrencyHelper
import com.test.app.ui.list.OnClickRatesItemObservable
import com.test.app.ui.list.OnTextWatcherObservable
import com.test.app.ui.list.RatesItem
import com.test.app.ui.list.isEqual
import com.test.app.ui.list.toBigDecimalOrZero
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import java.math.BigDecimal
import java.math.MathContext
import java.util.Collections
import java.util.Currency
import java.util.concurrent.TimeUnit

private const val DELAY_IN_SEC = 1L

class RatesViewModel(
    private val repository: CurrencyRateRepository,
    private val resourcesProvider: ResourcesProvider,
    private val onClickRatesItemObservable: OnClickRatesItemObservable,
    private val onTextWatcherObservable: OnTextWatcherObservable,
    val adapter: RatesListAdapter
) {
    // Trade off memory for better performance
    private val flagIconsCache = ArrayMap<String, Int>()
    private val displayNamesCache = ArrayMap<String, String>()

    // A valve to turn on/off updating currency rate
    private val valveSubject = BehaviorSubject.create<RatesItem>()

    fun observeRateTextChange(): Observable<List<RatesItem>> {
        return onTextWatcherObservable.observeRateChange()
            .doOnNext { Timber.w("Text changed %s", it) }
            .doOnNext { emitRatesItem(EMPTY_RATES_ITEM) } // Stop spamming server
            .subscribeOn(Schedulers.computation())
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
                        .delay(DELAY_IN_SEC, TimeUnit.SECONDS, Schedulers.computation())
                        .doOnSuccess { emitRatesItem(baseItem.copy(rate = newBase)) }
                }
            }
    }

    fun observeOnItemClick(): Observable<Any> {
        return onClickRatesItemObservable.observeClickItem()
            .doOnNext { emitRatesItem(EMPTY_RATES_ITEM) } // Stop spamming server
            .observeOn(AndroidSchedulers.mainThread())
            .switchMapSingle { item: RatesItem ->
                adapter.moveSelectedItemToTop(item)
                    .flatMap {
                        if (it.isNotEmpty()) {
                            Single.just(it).compose(adapter.asRxTransformer().forSingle())
                        } else {
                            Single.just(it)
                        }
                    }
                    .doOnSuccess {
                        emitRatesItem(item)
                    }
            }
    }

    fun observeGetCurrencyRatesInterval(): Observable<List<RatesItem>> {
        return valveSubject.hide()
            .distinctUntilChanged()
            .doOnNext { Timber.d("valve value %s", it.toString()) }
            .subscribeOn(Schedulers.computation())
            .switchMap { item: RatesItem ->
                Observable.just(item)
                    .takeWhile { it != EMPTY_RATES_ITEM } // Not stop spamming server signal
                    .switchMapSingle { validItem: RatesItem ->
                        getCurrencyRates(validItem.code, validItem.rate)
                            .onErrorReturnItem(Collections.emptyList()) // Simply swallow error
                            .doOnSuccess { Timber.d("List size %d", it.size) }
                    }
                    .delay(DELAY_IN_SEC, TimeUnit.SECONDS, Schedulers.computation())
                    .repeat()
            }
            .startWith(repository.getPlaceHolderRates())
            .filter { it.isNotEmpty() }
            .compose(adapter.asRxTransformer().forObservable())
    }

    private fun emitRatesItem(item: RatesItem) {
        Timber.d("Emit item %s", item.toString())
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