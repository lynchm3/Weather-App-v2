package com.marklynch.weather.di

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.location.LocationManager
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.analytics.FirebaseAnalytics
import com.marklynch.weather.BuildConfig
import com.marklynch.weather.data.WeatherDatabase
import com.marklynch.weather.repository.location.LocationRepository
import com.marklynch.weather.repository.network.NetworkInfoLiveData
import com.marklynch.weather.repository.weather.WeatherRepository
import com.marklynch.weather.utils.PermissionsChecker
import com.marklynch.weather.utils.TimberDebugTree
import com.marklynch.weather.utils.TimberProductionTree
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.koin.dsl.module.module
import org.koin.experimental.builder.single
import timber.log.Timber

private val appModule = module {

    single<Timber.Tree> {
        if (BuildConfig.DEBUG) {
            TimberDebugTree()
        } else {
            TimberProductionTree()
        }
    }

}

@SuppressLint("CommitPrefEdits")
private val dataModule = module {

    single {
        Room.databaseBuilder(
            get<Application>(),
            WeatherDatabase::class.java, "weather.db"
        ).build()
    }

    factory { (baseUrl: String) ->
        baseUrl.toHttpUrlOrNull() ?: throw IllegalArgumentException("Illegal URL: $baseUrl")
    }

    single<SharedPreferences> {
        PreferenceManager.getDefaultSharedPreferences(get())
    }

    single<SharedPreferences.Editor> {
        get<SharedPreferences>().edit()
    }

    single<FusedLocationProviderClient> {
        LocationServices.getFusedLocationProviderClient(get<Context>())
    }

    single {
        PermissionsChecker()
    }

    single {
        get<Context>().getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    single<LocationRepository>()
    single<WeatherRepository>()
    single<NetworkInfoLiveData>()

    single() {
        FirebaseAnalytics.getInstance(get())
    }
}

val appModules = listOf(
    appModule,
    dataModule
)