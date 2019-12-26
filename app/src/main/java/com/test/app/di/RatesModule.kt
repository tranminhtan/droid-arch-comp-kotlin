package com.test.app.di

import com.test.app.annotation.ActivityScoped
import com.test.app.base.ResourcesProviderImpl
import com.test.app.service.CurrencyRateRepositoryImpl
import com.test.app.service.CurrencyRateService
import com.test.app.ui.RatesActivity
import com.test.app.ui.RatesListAdapter
import com.test.app.ui.RatesViewModel
import com.test.app.ui.list.OnClickRatesItemStream
import com.test.app.ui.list.RatesItemViewModel
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
    fun provideOnClickItemStream() = OnClickRatesItemStream()

    @JvmStatic
    @ActivityScoped
    @Provides
    fun provideAdapter(onClickRatesItemStream: OnClickRatesItemStream) = RatesListAdapter(RatesItemViewModel(onClickRatesItemStream))

    @JvmStatic
    @ActivityScoped
    @Provides
    fun provideRatesViewModel(
        activity: RatesActivity,
        currencyRateService: CurrencyRateService,
        onClickRatesItemStream: OnClickRatesItemStream,
        adapter: RatesListAdapter
    ): RatesViewModel {
        return RatesViewModel(
            CurrencyRateRepositoryImpl(currencyRateService),
            ResourcesProviderImpl(activity),
            onClickRatesItemStream,
            adapter
        )
    }
}
