package com.task.app.ui

import androidx.annotation.DrawableRes
import androidx.collection.ArrayMap
import com.task.app.base.ResourcesProvider
import com.task.app.service.CurrencyRateRepository
import com.task.app.ui.list.RatesItem
import com.task.app.ui.support.CurrencyHelper
import com.task.app.ui.support.isEqual
import com.task.app.ui.support.toBigDecimalOrZero
import io.reactivex.Observable
import io.reactivex.Single
import java.math.BigDecimal
import java.math.MathContext
import java.util.Currency

class GetServerRatesItemUseCase(
    private val repository: CurrencyRateRepository,
    private val resourcesProvider: ResourcesProvider
) {

    // Trade off memory for better performance
    private val flagIconsCache = ArrayMap<String, Int>()
    private val displayNamesCache = ArrayMap<String, String>()

    // Get currency rates and convert to RatesItem list
    fun getCurrencyRates(baseCode: String, baseRate: String): Single<List<RatesItem>> {
        return repository.getCurrencyRates(baseCode)
            .flatMapObservable { Observable.fromIterable(it.entries) }
            .map { entry -> toRatesItem(entry, baseRate) }
            .collectInto(initListWithBaseItem(baseCode, baseRate), { list, item -> list.add(item) })
            .map { it }
    }

    fun calculateNewRate(newBase: String, currBase: String, currRate: String): String {
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