package com.marklynch.weather

import com.marklynch.weather.dependencyinjection.activityModules
import com.marklynch.weather.dependencyinjection.appModules
import com.marklynch.weather.dependencyinjection.mockModuleApplication
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