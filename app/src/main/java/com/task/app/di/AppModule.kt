package com.task.app.di

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.task.app.base.SchedulersProvider
import com.task.app.base.SchedulersProviderImpl
import com.task.app.moshi.MoshiProvider
import com.task.app.service.RetrofitProvider
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
object AppModule {
    @JvmStatic
    @Singleton
    @Provides
    fun provideSchedulersProvider(): SchedulersProvider = SchedulersProviderImpl()

    @JvmStatic
    @Singleton
    @Provides
    fun provideMoshi(factories: Set<@JvmSuppressWildcards JsonAdapter.Factory>): Moshi {
        return MoshiProvider(factories).getMoshi()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideRetrofit(moshi: Moshi, schedulersProvider: SchedulersProvider): Retrofit {
        return RetrofitProvider(moshi, schedulersProvider.io()).getRetrofit()
    }
}