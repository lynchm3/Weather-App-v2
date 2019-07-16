package com.marklynch.weather

import android.app.Application
import android.content.Context
import com.marklynch.weather.di.activityModules
import com.marklynch.weather.di.appModules
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.test.KoinTest
import org.koin.test.checkModules
import org.mockito.Mockito.mock


class DependencyGraphTest: KoinTest {

    @Test
    fun checkDependencyGraph() {

        val mockApplication = module(override = true) {
            single { mock(Application::class.java) }
            single { mock(Context::class.java) }
        }

        val moduleList = appModules + activityModules + mockApplication

        checkModules(moduleList)
    }
}