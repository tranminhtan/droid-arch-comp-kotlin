package com.test.app.ui

import androidx.annotation.DrawableRes
import androidx.annotation.VisibleForTesting
import androidx.collection.ArrayMap
import com.test.app.base.DataBindingRecyclerViewAdapter
import com.test.app.base.ResourcesProvider
import com.test.app.model.CurrencyRateResponse
import com.test.app.service.CurrencyRateRepository
import com.test.app.ui.list.OnClickRatesItemStream
import com.test.app.ui.list.RatesItem
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
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
    val adapter: DataBindingRecyclerViewAdapter<RatesItem>
) {
    private val flagIconsCache: MutableMap<String, Int> = ArrayMap()
    private val displayNamesCache: MutableMap<String, String> = ArrayMap()

    fun updateCurrencyRatesInterval(): Observable<List<RatesItem>> {
        val intervalObs = Observable.interval(0, INTERVAL_IN_SEC, TimeUnit.SECONDS, Schedulers.computation())
        val onClickItemObs = onClickRatesItemStream.observeClickItem().distinctUntilChanged()

        return Observable.combineLatest(intervalObs, onClickItemObs, BiFunction { _: Long, item: RatesItem -> item })
            .subscribeOn(Schedulers.computation())
            .switchMapSingle {
                getCurrencyRates(it.code, it.rate)
                    .onErrorReturnItem(Collections.emptyList()) // Simply swallow error
            }
            .startWith(repository.getPlaceHolderRates()) // Display offline data first
            .filter { it.isNotEmpty() }
            .compose(adapter.asRxTransformer().forObservable())
    }

    @VisibleForTesting
    fun getCurrencyRates(baseCode: String, baseRate: String): Single<List<RatesItem>> {
        return repository.getCurrencyRates(baseCode)
            .flatMap { res: CurrencyRateResponse ->
                Observable.fromIterable(res.rates.entries)
                    .map { entry -> toRatesItem(entry, baseRate) }
                    .toList()
                    .map {
                        if (it.isEmpty()) {
                            it
                        } else {
                            // Add base currency as the first item of the list
                            val newList = ArrayList<RatesItem>(it.size + 1)
                            newList.add(toRatesItem(res.base, baseRate))
                            newList.addAll(it)
                            newList
                        }
                    }
            }
    }

    private fun toRatesItem(entry: Map.Entry<String, Double>, baseRate: String): RatesItem {
        val calculatedRate = BigDecimal(baseRate).multiply(BigDecimal(entry.value)).round(MathContext(5))
        return toRatesItem(entry.key, calculatedRate.toPlainString())
    }

    private fun toRatesItem(code: String, rate: String): RatesItem {
        return RatesItem(code, getDisplayName(code), rate, getFlagRes(code))
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