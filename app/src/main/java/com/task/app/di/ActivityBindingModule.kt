package com.task.app.di

import com.task.app.annotation.ActivityScoped
import com.task.app.ui.RatesActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface ActivityBindingModule {
    @ActivityScoped
    @ContributesAndroidInjector(modules = [RatesActivityModule::class])
    fun contributeRatesActivityInjector(): RatesActivity
}