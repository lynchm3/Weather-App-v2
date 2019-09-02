package com.marklynch.weather.livedata.sharedpreferences

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.marklynch.weather.dependencyinjection.*
import com.marklynch.weather.livedata.sharedpreferences.booleansharedpreference.BooleanSharedPreferencesLiveData
import com.marklynch.weather.utils.observeXTimes
import junit.framework.Assert.*
import org.junit.*
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.koin.standalone.StandAloneContext.stopKoin
import org.koin.test.KoinTest

class BooleanSharedPreferencesLiveDataTest : KoinTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        val moduleList =
            appModules +
                    activityModules +
                    mockModuleApplication +
                    mockModuleSharedPreferences +
                    mockModuleSharedPreferencesEditor
        loadKoinModules(moduleList)
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun `Test setting and observing true`() {

        val booleanSharedPreferencesLiveData =
            BooleanSharedPreferencesLiveData(
                ""
            )

        booleanSharedPreferencesLiveData.setSharedPreference(true)

        var observations = 0
        booleanSharedPreferencesLiveData.observeXTimes(1) {
            observations++
            assertTrue(it)
        }

        assertEquals(1,observations)

    }

    @Test
    fun `Test setting and observing false`() {

        val booleanSharedPreferencesLiveData =
            BooleanSharedPreferencesLiveData(
                ""
            )

        booleanSharedPreferencesLiveData.setSharedPreference(false)

        var observations = 0
        booleanSharedPreferencesLiveData.observeXTimes(1) {
            observations++
            assertFalse(it)
        }

        assertEquals(1,observations)
    }

}