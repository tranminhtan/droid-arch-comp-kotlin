package com.task.app.service

import com.task.app.TestBase
import com.task.app.model.CurrencyRateResponse
import io.reactivex.Single
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito
import java.util.Collections

class CurrencyRateRepositoryTest : TestBase() {
    @Mock
    lateinit var currencyRateService: CurrencyRateService
    private lateinit var currencyRateRepository: CurrencyRateRepository

    override fun setup() {
        super.setup()
        currencyRateRepository = CurrencyRateRepositoryImpl(currencyRateService)
    }

    @Test
    fun getCurrencyRate_baseNotMatched_throwException() {
        val base = "EUR"
        given(currencyRateService.getCurrencyRates(base))
            .willReturn(Single.just(CurrencyRateResponse("WrongBase", "", Collections.emptyMap())))

        currencyRateRepository.getCurrencyRates(base)
            .test()
            .assertError(IllegalStateException::class.java)
            .assertTerminated()
            .assertNoValues()
            .dispose()
    }

    @Test
    fun getCurrencyRate_baseMatched_noError() {
        val base = "EUR"
        val response = CurrencyRateResponse("EUR", "test", Collections.singletonMap("USD", 1.00))
        given(currencyRateService.getCurrencyRates(base))
            .willReturn(Single.just(response))

        currencyRateRepository.getCurrencyRates(base)
            .test()
            .assertNoErrors()
            .assertTerminated()
            .assertValue(Collections.singletonMap("USD", 1.00))
            .dispose()
    }
}