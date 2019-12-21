package com.test.app.di

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.test.app.moshi.MoshiProvider
import com.test.app.service.RetrofitProvider
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
object AppModule {

    @JvmStatic
    @Singleton
    @Provides
    fun provideMoshi(factories: Set<@JvmSuppressWildcards JsonAdapter.Factory>): Moshi {
        return MoshiProvider(factories).getMoshi()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideRetrofit(moshi: Moshi): Retrofit {
        return RetrofitProvider(moshi, Schedulers.io()).getRetrofit()
    }
}