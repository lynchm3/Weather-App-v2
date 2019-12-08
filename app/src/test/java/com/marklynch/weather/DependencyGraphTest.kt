package com.marklynch.weather

import com.marklynch.weather.di.activityModules
import com.marklynch.weather.di.appModules
import com.marklynch.weather.di.mockModuleApplication
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.checkModules

class DependencyGraphTest : KoinTest {

    @Test
    fun checkDependencyGraph() {
        val moduleList = appModules + activityModules + mockModuleApplication
        checkModules(moduleList)
    }
}