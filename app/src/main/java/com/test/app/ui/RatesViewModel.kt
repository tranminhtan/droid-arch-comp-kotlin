package com.test.app.ui

import androidx.annotation.DrawableRes
import androidx.collection.ArrayMap
import com.test.app.base.ResourcesProvider
import com.test.app.service.CurrencyRateRepository
import com.test.app.service.CurrencyRateRepository.Companion.EMPTY_RATES_ITEM
import com.test.app.ui.list.OnClickRatesItemStream
import com.test.app.ui.list.RatesItem
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import java.math.BigDecimal
import java.math.MathContext
import java.util.Collections
import java.util.Currency
import java.util.concurrent.TimeUnit

private const val INTERVAL_IN_SEC = 1L

class RatesViewModel(
    private val repository: CurrencyRateRepository,
    private val resourcesProvider: ResourcesProvider,
    private val onClickRatesItemStream: OnClickRatesItemStream,
    val adapter: RatesListAdapter
) {
    private val flagIconsCache = ArrayMap<String, Int>()
    private val displayNamesCache = ArrayMap<String, String>()
    private val valveSubject = BehaviorSubject.create<RatesItem>()

    fun observeOnItemClick(): Observable<Any> {
        return onClickRatesItemStream.observeClickItem()
            .distinctUntilChanged()
            .doOnNext { emitRatesItem(EMPTY_RATES_ITEM) } // Stop updating currency rates
            .switchMapSingle { item: RatesItem ->
                adapter.moveSelectedItemToTop(item).flatMap { hasMoved: Boolean ->
                    Timber.d("Moved Item %s", hasMoved)
                    val delay = if (hasMoved) 500L else 0 // Delay a bit for better UX
                    Single.timer(delay, TimeUnit.MILLISECONDS, Schedulers.computation())
                        .doOnSuccess { emitRatesItem(item) }
                }
            }
    }

    fun observeGetCurrencyRatesWithInterval(): Observable<List<RatesItem>> {
        return valveSubject.hide()
            .doOnNext { Timber.d("valve value %s", it.toString()) }
            .switchMap { item: RatesItem ->
                Observable.just(item)
                    .takeWhile { it != EMPTY_RATES_ITEM }
                    .switchMap { validItem: RatesItem ->
                        intervalObs()
                            .switchMapSingle {
                                getCurrencyRates(validItem.code, validItem.rate)
                                    .onErrorReturnItem(Collections.emptyList()) // Simply swallow error
                            }
                    }
                    .doOnNext { Timber.d("After call API %s", it.toString()) }
            }
            .subscribeOn(Schedulers.computation())
            .startWith(repository.getPlaceHolderRates()) // Display offline data first
            .filter { it.isNotEmpty() }
            .compose(adapter.asRxTransformer().forObservable())
    }

    private fun emitRatesItem(item: RatesItem) {
        Timber.d("Emit item %s", item.toString())
        valveSubject.onNext(item)
    }

    private fun intervalObs(): Observable<Long> {
        return Observable.interval(0, INTERVAL_IN_SEC, TimeUnit.SECONDS, Schedulers.computation())
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
        val calculatedRate = BigDecimal(baseRate).multiply(BigDecimal(entry.value)).round(MathContext(5))
        return toRatesItem(entry.key, calculatedRate.toPlainString())
    }

    private fun toRatesItem(code: String, rate: String, enabled: Boolean = false): RatesItem {
        return RatesItem(code, getDisplayName(code), rate, getFlagRes(code), enabled)
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