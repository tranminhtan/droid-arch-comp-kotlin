package com.test.app.service

import com.test.app.model.CurrencyRateResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyRateService {
    @GET("latest")
    fun getCurrencyRates(@Query("base") base: String): Single<CurrencyRateResponse>
}