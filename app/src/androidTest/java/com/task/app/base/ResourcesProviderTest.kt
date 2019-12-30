package com.task.app.base

import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.task.app.MyApplication
import com.task.app.R
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class ResourcesProviderTest {
    private lateinit var resourcesProvider: ResourcesProvider

    @Before
    fun init() {
        resourcesProvider = ResourcesProviderImpl(getApplicationContext<MyApplication>())
    }

    @Test
    fun getDrawableResId() {
        Assert.assertEquals(R.drawable.ic_flag_eur, resourcesProvider.getDrawableResId("EUR"))
        Assert.assertEquals(R.drawable.ic_flag_eur, resourcesProvider.getDrawableResId("eur"))
        Assert.assertEquals(R.drawable.ic_flag_gbp, resourcesProvider.getDrawableResId("GBP"))
        Assert.assertEquals(R.drawable.ic_flag_gbp, resourcesProvider.getDrawableResId("gbp"))

        Assert.assertEquals(R.drawable.ic_place_holder, resourcesProvider.getDrawableResId("test"))
        Assert.assertEquals(R.drawable.ic_place_holder, resourcesProvider.getDrawableResId(""))
    }
}