package com.marklynch.weather.di

import android.content.SharedPreferences
import android.preference.PreferenceManager
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

    single <BooleanSharedPreferencesLiveData> { (sharedPreferencesKey: String) ->
        BooleanSharedPreferencesLiveData(
            sharedPreferencesKey
        )
    }

    single <SharedPreferences>{
        PreferenceManager.getDefaultSharedPreferences(get())
    }

    single <SharedPreferences.Editor>{
        get<SharedPreferences>().edit()
    }

    single<LocationLiveData>()
    single<WeatherLiveData>()
    single<NetworkInfoLiveData>()
}

val appModules = listOf(appModule, dataModule)