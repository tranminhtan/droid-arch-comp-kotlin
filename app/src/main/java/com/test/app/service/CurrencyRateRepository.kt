package com.test.app.service

import com.test.app.R
import com.test.app.ui.list.RatesItem
import io.reactivex.Single
import java.util.Currency

interface CurrencyRateRepository {
    companion object {
        val BASE_RATES_ITEM = RatesItem("EUR", Currency.getInstance("EUR").displayName, "100", R.drawable.ic_flag_eur)
        val EMPTY_RATES_ITEM = RatesItem("", "", "", 0)
    }

    fun getCurrencyRates(base: String): Single<Map<String, Double>>

    fun getPlaceHolderRates(): List<RatesItem>
}

class CurrencyRateRepositoryImpl(private val currencyRateService: CurrencyRateService) :
    CurrencyRateRepository {

    override fun getCurrencyRates(base: String): Single<Map<String, Double>> {
        return currencyRateService.getCurrencyRates(base)
            .map {
                if (it.base != base)
                    throw IllegalStateException("Response's base doesn't match")
                else it.rates
            }
    }

    override fun getPlaceHolderRates(): List<RatesItem> {
        return listOf(
            CurrencyRateRepository.BASE_RATES_ITEM.copy(rate = ""),
            RatesItem("AUD", Currency.getInstance("AUD").displayName, "", R.drawable.ic_flag_aud),
            RatesItem("BGN", Currency.getInstance("BGN").displayName, "", R.drawable.ic_flag_bgn),
            RatesItem("BRL", Currency.getInstance("BRL").displayName, "", R.drawable.ic_flag_brl),
            RatesItem("CAD", Currency.getInstance("CAD").displayName, "", R.drawable.ic_flag_cad),
            RatesItem("CNY", Currency.getInstance("CNY").displayName, "", R.drawable.ic_flag_cny)
        )
    }
}