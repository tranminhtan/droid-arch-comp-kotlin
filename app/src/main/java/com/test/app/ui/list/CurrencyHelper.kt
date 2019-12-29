package com.test.app.ui.list

import java.math.BigDecimal
import java.text.NumberFormat
import java.text.ParseException

object CurrencyHelper {
    private val formatter = NumberFormat.getNumberInstance()

    init {
        formatter.maximumFractionDigits = 2
    }

    fun toBigDecimal(value: String): BigDecimal {
        return try {
            BigDecimal(formatter.parse(value)!!.toDouble())
        } catch (e: ParseException) {
            BigDecimal.ZERO
        }
    }

    fun format(value: BigDecimal): String = formatter.format(value)
}

fun BigDecimal.isEqual(that: BigDecimal): Boolean = this.compareTo(that) == 0

fun String.toBigDecimalOrZero(): BigDecimal = CurrencyHelper.toBigDecimal(this)


