package com.test.app.ui.list

import androidx.annotation.DrawableRes

data class RatesItem(
    val code: String,
    val displayName: String,
    val rate: String,
    @DrawableRes val flagIconRes: Int,
    val editable: Boolean = false
)
