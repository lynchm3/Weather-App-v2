package com.marklynch.weather.livedata.sharedpreferences

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.InstrumentationRegistry
import androidx.test.core.app.ApplicationProvider
import com.marklynch.weather.MainApplication
import com.marklynch.weather.livedata.observeXTimes
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class BooleanSharedPreferencesLiveDataTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Test
    fun `Test`() {

        val booleanKey = "Some boolean key"
        val booleanSharedPreferencesLiveData = BooleanSharedPreferencesLiveData(ApplicationProvider.getApplicationContext<MainApplication>(), booleanKey)

        Assert.assertNull(booleanSharedPreferencesLiveData.value)

        booleanSharedPreferencesLiveData.setSharedPreference(true)

        booleanSharedPreferencesLiveData.observeXTimes(1) {
            assertTrue(it)
        }

        booleanSharedPreferencesLiveData.setSharedPreference(false)

        booleanSharedPreferencesLiveData.observeXTimes(1) {
            assertFalse(it)
        }
    }

}