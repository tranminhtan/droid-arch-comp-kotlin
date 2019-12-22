package com.test.app.ui

import androidx.annotation.DrawableRes
import androidx.annotation.VisibleForTesting
import com.test.app.base.DataBindingRecyclerViewAdapter
import com.test.app.base.ResourcesProvider
import com.test.app.model.CurrencyRateResponse
import com.test.app.service.CurrencyRateRepository
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

private const val BASE_CURRENCY = "EUR"

class RatesViewModel(
    private val repository: CurrencyRateRepository,
    private val resourcesProvider: ResourcesProvider,
    val adapter: DataBindingRecyclerViewAdapter<RatesItem>
) {
    private val flagIconsCache: MutableMap<String, Int> = HashMap()

    fun updateCurrencyRates(): Observable<List<RatesItem>> {
        return Observable.interval(0, 1, TimeUnit.SECONDS, Schedulers.computation())
            .switchMapSingle {
                getCurrencyRates(BASE_CURRENCY).onErrorReturnItem(Collections.emptyList())
            }
            .compose(adapter.asRxTransformer().forObservable())
    }

    @VisibleForTesting
    fun getCurrencyRates(base: String): Single<List<RatesItem>> {
        return repository.getCurrencyRate(base)
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