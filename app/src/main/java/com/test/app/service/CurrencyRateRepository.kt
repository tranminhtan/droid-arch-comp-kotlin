package com.test.app.service

import com.test.app.R
import com.test.app.model.CurrencyRateResponse
import com.test.app.ui.RatesItem
import io.reactivex.Single
import java.util.*

// This repository will be more meaningful if we have diff data sources
interface CurrencyRateRepository {
    fun getCurrencyRates(base: String): Single<CurrencyRateResponse>
    fun getPlaceHolderRates(): List<RatesItem>
}

class CurrencyRateRepositoryImpl(private val currencyRateService: CurrencyRateService) :
    CurrencyRateRepository {

    override fun getCurrencyRates(base: String): Single<CurrencyRateResponse> {
        return currencyRateService.getCurrencyRates(base)
            .map {
                if (it.base != base)
                    throw IllegalStateException("Response's base doesn't match")
                else it
            }
    }

    override fun getPlaceHolderRates(): List<RatesItem> {
        return listOf(
            RatesItem("EUR", Currency.getInstance("EUR").displayName, "", R.drawable.ic_flag_eur),
            RatesItem("USD", Currency.getInstance("USD").displayName, "", R.drawable.ic_flag_usd),
            RatesItem("CAD", Currency.getInstance("CAD").displayName, "", R.drawable.ic_flag_cad),
            RatesItem("GBP", Currency.getInstance("GBP").displayName, "", R.drawable.ic_flag_gbp),
            RatesItem("SGD", Currency.getInstance("SGD").displayName, "", R.drawable.ic_flag_sgd),
            RatesItem("CNY", Currency.getInstance("CNY").displayName, "", R.drawable.ic_flag_cny)
        )
    }
}