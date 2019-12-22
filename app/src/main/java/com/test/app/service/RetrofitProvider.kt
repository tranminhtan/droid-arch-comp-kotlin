package com.test.app.service

import com.squareup.moshi.Moshi
import io.reactivex.Scheduler
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

private const val HOST: String = "https://revolut.duckdns.org/"

class RetrofitProvider(
    private val moshi: Moshi,
    private val schedulers: Scheduler
) {

    fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(schedulers))
            .baseUrl(HOST)
            .build()
    }
}