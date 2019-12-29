package com.task.app.moshi

import com.squareup.moshi.JsonAdapter
import com.task.app.model.CurrencyRateResponse
import org.junit.Assert
import org.junit.Test

class MoshiProviderTest {

    @Test
    fun getMoshi_convertJsonString_toCurrencyRateResponse() {
        val factories = HashSet<JsonAdapter.Factory>(1)
        factories.add(RatesJsonAdapterFactory())

        val jsonAdapter: JsonAdapter<CurrencyRateResponse> =
            MoshiProvider(factories).getMoshi().adapter(CurrencyRateResponse::class.java)

        val response: CurrencyRateResponse? =
            jsonAdapter.fromJson("{\"base\":\"EUR\",\"date\":\"2018-09-06\",\"rates\":{\"AUD\":1.6145,\"BGN\":1.9535,\"BRL\":4.7862}}")

        Assert.assertEquals("EUR", response!!.base)
        Assert.assertEquals("2018-09-06", response.date)
        Assert.assertEquals(3, response.rates.size)
        Assert.assertEquals(1.6145, response.rates["AUD"])
        Assert.assertEquals(1.9535, response.rates["BGN"])
        Assert.assertEquals(4.7862, response.rates["BRL"])
    }
}