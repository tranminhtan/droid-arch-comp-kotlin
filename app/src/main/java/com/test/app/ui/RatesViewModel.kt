package com.test.app.ui

import androidx.annotation.DrawableRes
import androidx.annotation.VisibleForTesting
import androidx.collection.ArrayMap
import com.test.app.base.DataBindingRecyclerViewAdapter
import com.test.app.base.ResourcesProvider
import com.test.app.model.CurrencyRateResponse
import com.test.app.service.CurrencyRateRepository
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.Collections
import java.util.Currency
import java.util.concurrent.TimeUnit

private const val BASE_CURRENCY = "EUR"
private const val INTERVAL_IN_SEC = 1L

class RatesViewModel(
    private val repository: CurrencyRateRepository,
    private val resourcesProvider: ResourcesProvider,
    val adapter: DataBindingRecyclerViewAdapter<RatesItem>
) {
    private val flagIconsCache: MutableMap<String, Int> = ArrayMap()

    fun updateCurrencyRates(): Observable<List<RatesItem>> {
        return Observable.interval(0, INTERVAL_IN_SEC, TimeUnit.SECONDS, Schedulers.computation())
            .switchMapSingle {
                getCurrencyRates(BASE_CURRENCY)
                    .onErrorReturnItem(Collections.emptyList()) // Simply swallow error
            }
            .startWith(repository.getPlaceHolderRates())
            .filter { it.isNotEmpty() }
            .compose(adapter.asRxTransformer().forObservable())
    }

    @VisibleForTesting
    fun getCurrencyRates(base: String): Single<List<RatesItem>> {
        return repository.getCurrencyRates(base)
            .flatMap { res: CurrencyRateResponse ->
                Observable.fromIterable(res.rates.entries)
                    .map { entry -> toRatesItem(entry) }
                    .toList()
                    .map {
                        if (it.isEmpty()) {
                            it
                        } else {
                            // Add base currency as the first item of the list
                            val newList = ArrayList<RatesItem>(it.size + 1)
                            newList.add(toRatesItem(res.base, "1.00"))
                            newList.addAll(it)
                            newList
                        }
                    }
            }
    }

    private fun toRatesItem(entry: Map.Entry<String, Double>): RatesItem {
        return toRatesItem(entry.key, entry.value.toString())
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

    private fun getDisplayName(currencyCode: String): String {
        return Currency.getInstance(currencyCode).displayName
    }
}