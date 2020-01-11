package com.task.app.ui

import com.task.app.service.CurrencyRateRepository

class GetPlaceHolderRatesItemUseCase(private val repository: CurrencyRateRepository) {

    fun getPlaceHolderRates() = repository.getPlaceHolderRates()
}