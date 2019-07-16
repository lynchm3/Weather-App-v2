package com.marklynch.weather.di

import com.marklynch.weather.BuildConfig
import com.marklynch.weather.livedata.sharedpreferences.BooleanSharedPreferencesLiveData
import com.marklynch.weather.livedata.sharedpreferences.SharedPreferencesLiveData
import com.marklynch.weather.log.ProductionTree
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module
import org.koin.experimental.builder.single
import timber.log.Timber


private val appModule = module {

    single {
        if (BuildConfig.DEBUG) Timber.DebugTree()
        else ProductionTree()
    }

}

private val dataModule = module {
    single<BooleanSharedPreferencesLiveData>()
    single<BooleanSharedPreferencesLiveData>()
}

val appModules = listOf(appModule, dataModule)