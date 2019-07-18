package com.marklynch.weather.livedata.network

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.marklynch.weather.livedata.observeXTimes
import com.marklynch.weather.livedata.webresource.RawWebResourceLiveData
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest


class RawWebResourceLiveDataTest : KoinTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Test
    fun testOnActive() {
        val rawWebResourceLiveData = RawWebResourceLiveData()
        assertNull("Check live data value is null to start with", rawWebResourceLiveData.value)
        validateSingleObservation(rawWebResourceLiveData)
    }

    @Test
    fun testFetchWebResource() {
        val rawWebResourceLiveData = RawWebResourceLiveData()
        assertNull("Check live data value is null to start with", rawWebResourceLiveData.value)
        validateSingleObservation(rawWebResourceLiveData)
        rawWebResourceLiveData.fetchRawWebResource()
        validateSingleObservation(rawWebResourceLiveData)
    }

    private fun validateSingleObservation(rawWebResourceLiveData: RawWebResourceLiveData) {

        var observeCount = 0
        rawWebResourceLiveData.observeXTimes(1) {
            assertNotNull(it)
            observeCount++
        }

        val timeout = 10_000L
        val sleepInterval = 100L
        var waitingTime = 0L
        while (observeCount == 0 && waitingTime < timeout) {
            Thread.sleep(sleepInterval)
            waitingTime += sleepInterval
        }

        assertEquals(1, observeCount)
        assertFalse("Check live data has no more observers", rawWebResourceLiveData.hasActiveObservers())
    }
}
