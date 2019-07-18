package com.marklynch.weather.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.marklynch.weather.di.activityModules
import com.marklynch.weather.di.appModules
import com.marklynch.weather.livedata.sharedpreferences.BooleanSharedPreferencesLiveData
import com.marklynch.weather.mockApplication
import com.nhaarman.mockitokotlin2.*
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.koin.standalone.StandAloneContext.stopKoin
import org.koin.standalone.get
import org.koin.test.KoinTest
import org.mockito.ArgumentMatchers.anyBoolean


class MainViewModelTest : KoinTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    val mockModuleBooleanSharedPreferencesLiveData = module(override = true) {
        single {
            mock<BooleanSharedPreferencesLiveData> {
                on { value } doAnswer {
                    mockSharedPrefBoolean
                }
                on { setSharedPreference(anyBoolean()) } doAnswer {
                    mockSharedPrefBoolean = it.arguments[0] as Boolean
                    null
                }
            }
        }
    }

    @Before
    fun setup() {
        val moduleList = appModules + activityModules + mockApplication + mockModuleBooleanSharedPreferencesLiveData
        loadKoinModules(moduleList)
    }

    @After
    fun after() {
        stopKoin()
    }

    var mockSharedPrefBoolean = false

    @Test
    fun testSetUseCelsius() {
        val mainViewModel = MainViewModel(get())

        mainViewModel.setUseCelsius(true)
        assertTrue(mainViewModel.isUseCelsius() ?: false)

        mainViewModel.setUseCelsius(false)
        assertFalse(mainViewModel.isUseCelsius() ?: true)
    }

    @Test
    fun testSetUseKm() {
        val mainViewModel = MainViewModel(get())

        mainViewModel.setUseKm(true)
        assertTrue(mainViewModel.isUseKm() ?: false)

        mainViewModel.setUseKm(false)
        assertFalse(mainViewModel.isUseKm() ?: true)
    }
}