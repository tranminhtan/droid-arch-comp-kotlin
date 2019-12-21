package com.test.app.di

import com.squareup.moshi.JsonAdapter
import com.test.app.moshi.RatesJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet

@Module
object JsonAdapterModule {

    @JvmStatic
    @Provides
    @IntoSet
    fun provideJsonAdapterFactory(): JsonAdapter.Factory = RatesJsonAdapterFactory()
}
