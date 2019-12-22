package com.test.app.moshi

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.test.app.model.CurrencyRateResponse
import com.test.app.model.CurrencyRateResponseJsonAdapter
import java.lang.reflect.Type

// Every module has one Factory
class RatesJsonAdapterFactory : JsonAdapter.Factory {
    override fun create(
        type: Type,
        annotations: MutableSet<out Annotation>,
        moshi: Moshi
    ): JsonAdapter<*>? {
        if (annotations.isNotEmpty()) return null
        return when (Types.getRawType(type)) {
            CurrencyRateResponse::class.java -> CurrencyRateResponseJsonAdapter(moshi).nullSafe()
            // More adapters mapping declared here

            else -> null
        }
    }
}