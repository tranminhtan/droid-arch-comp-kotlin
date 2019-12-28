package com.test.app.di

import com.test.app.annotation.ActivityScoped
import com.test.app.base.ResourcesProviderImpl
import com.test.app.service.CurrencyRateRepositoryImpl
import com.test.app.service.CurrencyRateService
import com.test.app.ui.RatesActivity
import com.test.app.ui.RatesListAdapter
import com.test.app.ui.RatesViewModel
import com.test.app.ui.list.OnClickRatesItemObservable
import com.test.app.ui.list.OnEditRateClickListener
import com.test.app.ui.list.OnTextWatcherObservable
import com.test.app.ui.list.RateTextWatcher
import com.test.app.ui.list.RatesItemViewModel
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
object RatesModule {

    @JvmStatic
    @ActivityScoped
    @Provides
    fun provideOnClickItemObs() = OnClickRatesItemObservable()

    @JvmStatic
    @ActivityScoped
    @Provides
    fun provideOnRateWatcherObs() = OnTextWatcherObservable()

    @JvmStatic
    @ActivityScoped
    @Provides
    fun provideAdapter(
        onClickRatesItemObservable: OnClickRatesItemObservable,
        onTextWatcherObservable: OnTextWatcherObservable
    ) = RatesListAdapter(
        RatesItemViewModel(
            onClickRatesItemObservable,
            RateTextWatcher(onTextWatcherObservable),
            OnEditRateClickListener()
        )
    )

    @JvmStatic
    @ActivityScoped
    @Provides
    fun provideRatesViewModel(
        activity: RatesActivity,
        retrofit: Retrofit,
        onClickRatesItemObservable: OnClickRatesItemObservable,
        onTextWatcherObservable: OnTextWatcherObservable,
        adapter: RatesListAdapter
    ): RatesViewModel {
        return RatesViewModel(
            CurrencyRateRepositoryImpl(retrofit.create(CurrencyRateService::class.java)),
            ResourcesProviderImpl(activity),
            onClickRatesItemObservable,
            onTextWatcherObservable,
            adapter
        )
    }
}
