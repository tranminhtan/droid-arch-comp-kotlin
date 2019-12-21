package com.test.app.ui

import com.test.app.model.CurrencyRateResponse
import com.test.app.service.CurrencyRateRepository
import io.reactivex.Single

class RatesViewModel(private val repository: CurrencyRateRepository) {

    fun getCurrencyRates(base: String): Single<CurrencyRateResponse> {
        return repository.getCurrencyRate(base)
    }
}