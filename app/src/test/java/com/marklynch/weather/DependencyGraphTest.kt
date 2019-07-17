package com.marklynch.weather

import android.app.Application
import android.content.Context
import com.marklynch.weather.di.activityModules
import com.marklynch.weather.di.appModules
import com.marklynch.weather.livedata.sharedpreferences.BooleanSharedPreferencesLiveData
import com.marklynch.weather.livedata.sharedpreferences.SharedPreferencesLiveData
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.test.KoinTest
import org.koin.test.checkModules
import org.mockito.Mockito.mock


class DependencyGraphTest: KoinTest {

    @Test
    fun checkDependencyGraph() {

        val moduleList = appModules + activityModules + mockLiveData

        checkModules(moduleList)
    }
}

val mockLiveData = module(override = true) {
    single {mock(BooleanSharedPreferencesLiveData::class.java)}
}