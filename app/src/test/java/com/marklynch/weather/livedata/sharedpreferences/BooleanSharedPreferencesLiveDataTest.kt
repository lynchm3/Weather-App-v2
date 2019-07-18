package com.marklynch.weather.livedata.sharedpreferences

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.marklynch.weather.di.*
import com.marklynch.weather.livedata.observeXTimes
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
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
    fun `Test`() {

        val booleanKey = "Some boolean key"
        val booleanSharedPreferencesLiveData =
            BooleanSharedPreferencesLiveData(
                booleanKey
            )

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