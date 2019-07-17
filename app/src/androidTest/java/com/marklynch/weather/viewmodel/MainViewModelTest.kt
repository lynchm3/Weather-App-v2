package com.marklynch.weather.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.marklynch.weather.MainApplication
import com.marklynch.weather.livedata.sharedpreferences.BooleanSharedPreferencesLiveData
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.koin.standalone.StandAloneContext.stopKoin
import org.koin.test.KoinTest
import org.mockito.Mockito

class MainViewModelTest : KoinTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    val mockModule = module(override = true) {
        single { Mockito.mock(BooleanSharedPreferencesLiveData::class.java) }
    }

    @Before
    fun setup() {
        val moduleList = mockModule
//        startKoin(moduleList)
        loadKoinModules(moduleList)
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun testSetUseCelsius() {

        val mainViewModel = MainViewModel(ApplicationProvider.getApplicationContext<MainApplication>())

        mainViewModel.setUseCelsius(false)
        assertFalse(mainViewModel.isUseCelsius() ?: true)

        mainViewModel.setUseCelsius(true)
        assertTrue(mainViewModel.isUseCelsius() ?: false)
    }

    @Test
    fun testSetUseKm() {

        val mainViewModel = MainViewModel(ApplicationProvider.getApplicationContext<MainApplication>())

        mainViewModel.setUseKm(false)
        assertFalse(mainViewModel.isUseKm() ?: true)

        mainViewModel.setUseKm(true)
        assertTrue(mainViewModel.isUseKm() ?: false)
    }

}