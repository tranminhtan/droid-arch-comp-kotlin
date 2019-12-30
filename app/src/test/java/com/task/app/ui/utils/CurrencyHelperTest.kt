package com.task.app.ui.utils

import com.task.app.TestBase
import org.junit.Assert
import org.junit.Test
import java.math.BigDecimal

class CurrencyHelperTest : TestBase() {
    @Test
    fun isEqual() {
        Assert.assertTrue(BigDecimal("1.00").isEqual(BigDecimal.ONE))
        Assert.assertTrue(BigDecimal("1.00").isEqual(BigDecimal("1.0")))
        Assert.assertTrue(BigDecimal("1.00").isEqual(BigDecimal("1.000")))
        Assert.assertFalse(BigDecimal("1.00").isEqual(BigDecimal.ZERO))
    }

    @Test
    fun toBigDecimalOrZero() {
        Assert.assertEquals(BigDecimal.ZERO, "".toBigDecimalOrZero())
        Assert.assertEquals(BigDecimal.ZERO, ".".toBigDecimalOrZero())
        Assert.assertEquals(BigDecimal.ZERO, ",".toBigDecimalOrZero())
        Assert.assertEquals(BigDecimal.ONE, "1".toBigDecimalOrZero())
    }

    @Test
    fun format() {
        Assert.assertEquals("1", CurrencyHelper.format(BigDecimal("1")))
        Assert.assertEquals("1.2", CurrencyHelper.format(BigDecimal("1.2")))
        Assert.assertEquals("1.23", CurrencyHelper.format(BigDecimal("1.23")))
        Assert.assertEquals("1.23", CurrencyHelper.format(BigDecimal("1.234")))
    }
}