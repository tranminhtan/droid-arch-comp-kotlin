package com.test.app.ui

import androidx.annotation.DrawableRes

data class RatesItem(
    val code: String,
    val displayName: String,
    val rate: String,
    @DrawableRes val flagIconRes: Int
)
