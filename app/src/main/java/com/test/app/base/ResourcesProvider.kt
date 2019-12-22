package com.test.app.base

import android.content.Context
import androidx.annotation.DrawableRes
import com.test.app.R
import timber.log.Timber
import java.util.*

private const val flagIconPrefix = "ic_flag_"

interface ResourcesProvider {
    @DrawableRes
    fun getDrawableResId(currencyCode: String): Int
}

class ResourcesProviderImpl(private val context: Context) : ResourcesProvider {
    @DrawableRes
    override fun getDrawableResId(currencyCode: String): Int {
        val resIcon = flagIconPrefix + currencyCode.toLowerCase(Locale.getDefault())

        return try {
            context.resources.getIdentifier(resIcon, "drawable", context.packageName)

        } catch (e: RuntimeException) {
            Timber.w("No flag icon for %s", resIcon)
            R.drawable.ic_place_holder
        }
    }
}
