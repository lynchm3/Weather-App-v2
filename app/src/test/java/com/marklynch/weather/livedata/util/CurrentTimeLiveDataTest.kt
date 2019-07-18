package com.marklynch.weather.livedata.util

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.marklynch.weather.livedata.observeXTimes
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest


class CurrentTimeLiveDataTest : KoinTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Test
    fun testOnActive() {
        val currentTimeLiveData = CurrentTimeLiveData()
        assertNull("Check live data value is null to start with", currentTimeLiveData.value)

        var observeCount = 0
        currentTimeLiveData.observeXTimes (1) {
            assertTrue("Check live data value is close or equal to current time",System.currentTimeMillis() - it < 1000)
            observeCount++
        }
        assertEquals(1,observeCount)
        assertFalse("Check live data has no more observers", currentTimeLiveData.hasActiveObservers())
    }

    @Test
    fun testIncrementalTime() {
        val currentTimeLiveData = CurrentTimeLiveData()
        assertNull("Check live data value is null to start with", currentTimeLiveData.value)

        val incrementsToCheck = 3
        var previousTime = 0L
        var observeCount = 0
        currentTimeLiveData.observeXTimes (3) {
            assertTrue(it>previousTime)
            assertTrue("Check live data value is close or equal to current time",System.currentTimeMillis() - it < 1000)
            previousTime = it
            observeCount++
        }

        Thread.sleep(incrementsToCheck * 1_000L)
        assertEquals(incrementsToCheck, observeCount)
        assertFalse("Check live data has no more observers", currentTimeLiveData.hasActiveObservers())
    }
}
