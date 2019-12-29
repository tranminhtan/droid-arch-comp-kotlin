package com.task.app.di

import com.task.app.annotation.ActivityScoped
import com.task.app.base.ResourcesProviderImpl
import com.task.app.service.CurrencyRateRepositoryImpl
import com.task.app.service.CurrencyRateService
import com.task.app.ui.RatesActivity
import com.task.app.ui.RatesViewModel
import com.task.app.ui.list.RateTextWatcher
import com.task.app.ui.list.RatesItemViewModel
import com.task.app.ui.list.RatesListAdapter
import com.task.app.ui.utils.OnClickRatesItemObservable
import com.task.app.ui.utils.OnEditRateClickListener
import com.task.app.ui.utils.OnTextWatcherObservable
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
object RatesActivityModule {

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
