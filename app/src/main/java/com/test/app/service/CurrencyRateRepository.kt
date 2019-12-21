package com.test.app.service

import com.test.app.model.CurrencyRateResponse
import io.reactivex.Single

// This repository will be more meaningful if we have diff data sources
interface CurrencyRateRepository {
    fun getCurrencyRate(base: String): Single<CurrencyRateResponse>
}

class CurrencyRateRepositoryImpl(private val currencyRateService: CurrencyRateService) : CurrencyRateRepository {
    override fun getCurrencyRate(base: String): Single<CurrencyRateResponse> {
        return currencyRateService.getCurrencyRates(base)
            .map {
                if (it.base != base)
                    throw IllegalStateException("Response's base doesn't match")
                else it
            }
    }
}