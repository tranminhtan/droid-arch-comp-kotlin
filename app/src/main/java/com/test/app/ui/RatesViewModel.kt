package com.test.app.ui

import android.content.Context
import androidx.annotation.DrawableRes
import com.test.app.model.CurrencyRateResponse
import com.test.app.service.CurrencyRateRepository
import io.reactivex.Single
import java.util.Currency
import java.util.Locale

private const val flagIconPrefix = "ic_flag_"

class RatesViewModel(private val repository: CurrencyRateRepository) {

    fun getCurrencyRates(base: String): Single<CurrencyRateResponse> {
        return repository.getCurrencyRate(base)
    }

    fun getDisplayName(currencyCode: String): String {
        return Currency.getInstance(currencyCode).displayName
    }

    @DrawableRes
    fun getDrawableResId(context: Context, currencyCode: String): Int {
        val resIcon = flagIconPrefix + currencyCode.toLowerCase(Locale.getDefault())
        return context.resources.getIdentifier(resIcon, "drawable", context.packageName)
    }
}