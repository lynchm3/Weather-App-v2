package com.marklynch.weather.livedata.util

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test



class CurrentTimeLiveDataTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private fun <T> LiveData<T>.observeXTimes(x:Int, onChangeHandler: (T) -> Unit) {
        val observer = LimitedObserver(x, handler = onChangeHandler)
        observe(observer, observer)
    }

    @Test
    fun testInitialTime() {
        val currentTimeLiveData = CurrentTimeLiveData()
        assertNull("Check live data value is null to start with", currentTimeLiveData.value)

        currentTimeLiveData.observeXTimes (1) {
            assertTrue("Check live data value is close or equal to current time",System.currentTimeMillis() - it < 1000)
        }
        assertFalse("Check live data has no more observers", currentTimeLiveData.hasActiveObservers())
    }

    @Test
    fun testIncrementalTime() {
        val currentTimeLiveData = CurrentTimeLiveData()
        assertNull("Check live data value is null to start with", currentTimeLiveData.value)

        val incrementsToCheck = 3
        val previousTime = 0L
        currentTimeLiveData.observeXTimes (3) {
            assertTrue(it>previousTime)
            assertTrue("Check live data value is close or equal to current time",System.currentTimeMillis() - it < 1000)
        }

        Thread.sleep(incrementsToCheck * 1_000L)
        assertFalse("Check live data has no more observers", currentTimeLiveData.hasActiveObservers())
    }
}
