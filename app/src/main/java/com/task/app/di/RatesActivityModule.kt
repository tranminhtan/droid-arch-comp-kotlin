package com.task.app.di

import com.task.app.annotation.ActivityScoped
import com.task.app.base.ResourcesProviderImpl
import com.task.app.base.SchedulersProvider
import com.task.app.service.CurrencyRateRepositoryImpl
import com.task.app.service.CurrencyRateService
import com.task.app.ui.RatesActivity
import com.task.app.ui.RatesViewModel
import com.task.app.ui.list.RateTextWatcher
import com.task.app.ui.list.RatesAdapter
import com.task.app.ui.list.RatesItemViewModel
import com.task.app.ui.support.OnClickRatesItemObservable
import com.task.app.ui.support.OnEditRateClickListener
import com.task.app.ui.support.OnTextWatcherObservable
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
    fun provideOnRateWatcherObs(schedulersProvider: SchedulersProvider) = OnTextWatcherObservable(schedulersProvider)

    @JvmStatic
    @ActivityScoped
    @Provides
    fun provideAdapter(
        onClickRatesItemObservable: OnClickRatesItemObservable,
        onTextWatcherObservable: OnTextWatcherObservable
    ) = RatesAdapter(
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
        schedulersProvider: SchedulersProvider,
        activity: RatesActivity,
        retrofit: Retrofit,
        onClickRatesItemObservable: OnClickRatesItemObservable,
        onTextWatcherObservable: OnTextWatcherObservable,
        adapter: RatesAdapter
    ): RatesViewModel {
        return RatesViewModel(
            schedulersProvider,
            CurrencyRateRepositoryImpl(retrofit.create(CurrencyRateService::class.java)),
            ResourcesProviderImpl(activity),
            onClickRatesItemObservable,
            onTextWatcherObservable,
            adapter
        )
    }
}
