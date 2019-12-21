package com.test.app.di

import com.test.app.annotation.ActivityScoped
import com.test.app.service.CurrencyRateRepositoryImpl
import com.test.app.service.CurrencyRateService
import com.test.app.ui.RatesViewModel
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
object RatesModule {

    @JvmStatic
    @ActivityScoped
    @Provides
    fun provideCurrencyRateService(retrofit: Retrofit): CurrencyRateService {
        return retrofit.create(CurrencyRateService::class.java)
    }

    @JvmStatic
    @ActivityScoped
    @Provides
    fun provideRatesViewModel(currencyRateService: CurrencyRateService) =
        RatesViewModel(CurrencyRateRepositoryImpl(currencyRateService))
}