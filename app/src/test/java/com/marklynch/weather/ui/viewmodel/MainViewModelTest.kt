package com.marklynch.weather.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
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
        val moduleList = appModules + activityModules + mockModuleApplication
        loadKoinModules(moduleList)
    }

    @After
    fun after() {
        stopKoin()
    }
}