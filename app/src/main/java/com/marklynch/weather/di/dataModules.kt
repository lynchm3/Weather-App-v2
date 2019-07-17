package com.marklynch.weather.di

import com.marklynch.weather.BuildConfig
import com.marklynch.weather.livedata.location.LocationLiveData
import com.marklynch.weather.livedata.network.NetworkInfoLiveData
import com.marklynch.weather.livedata.sharedpreferences.BooleanSharedPreferencesLiveData
import com.marklynch.weather.livedata.weather.WeatherLiveData
import com.marklynch.weather.log.ProductionTree
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
    single { (sharedPreferencesKey: String) ->
        BooleanSharedPreferencesLiveData(
            get(),
            sharedPreferencesKey
        )
    }

    single<LocationLiveData>()
    single<WeatherLiveData>()
    single<NetworkInfoLiveData>()
}

val appModules = listOf(appModule, dataModule)