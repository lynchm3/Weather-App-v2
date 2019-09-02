package com.marklynch.weather.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.marklynch.weather.dependencyinjection.activityModules
import com.marklynch.weather.dependencyinjection.appModules
import com.marklynch.weather.di.*
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.koin.standalone.StandAloneContext.stopKoin
import org.koin.standalone.get
import org.koin.test.KoinTest

class MainViewModelTest : KoinTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        val moduleList = appModules + activityModules + mockModuleApplication +
                mockModuleUse24hrClockSharedPreferenceLiveData +
                mockModuleUseCelsiusSharedPreferenceLiveData +
                mockModuleUseKmSharedPreferenceLiveData
        loadKoinModules(moduleList)
    }

    @After
    fun after() {
        stopKoin()
    }

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