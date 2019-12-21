package com.test.app.di

import com.test.app.annotation.ActivityScoped
import com.test.app.ui.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface ActivityBindingModule {
    @ActivityScoped
    @ContributesAndroidInjector(modules = [MainModule::class])
    fun contributeMainActivityInjector(): MainActivity
}