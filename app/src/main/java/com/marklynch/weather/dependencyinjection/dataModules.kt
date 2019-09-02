package com.marklynch.weather.dependencyinjection

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.location.LocationManager
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.marklynch.weather.BuildConfig
import com.marklynch.weather.data.WeatherDatabase
import com.marklynch.weather.livedata.db.ManualLocationRepository
import com.marklynch.weather.livedata.location.LocationLiveData
import com.marklynch.weather.livedata.network.NetworkInfoLiveData
import com.marklynch.weather.livedata.sharedpreferences.booleansharedpreference.Use24hrClockSharedPreferenceLiveData
import com.marklynch.weather.livedata.sharedpreferences.booleansharedpreference.UseCelsiusSharedPreferenceLiveData
import com.marklynch.weather.livedata.sharedpreferences.booleansharedpreference.UseKmSharedPreferenceLiveData
import com.marklynch.weather.livedata.sharedpreferences.longsharedpreference.CurrentLocationIdSharedPreferenceLiveData
import com.marklynch.weather.livedata.sharedpreferences.longsharedpreference.LongSharedPreferencesLiveData
import com.marklynch.weather.livedata.weather.WeatherLiveData
import com.marklynch.weather.log.ProductionTree
import com.marklynch.weather.utils.PermissionsChecker
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.koin.dsl.module.module
import org.koin.experimental.builder.single
import timber.log.Timber

private val appModule = module {

    single {
        if (BuildConfig.DEBUG) Timber.DebugTree()
        else ProductionTree()
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

    factory<HttpUrl> { (baseUrl: String) ->
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

    single<PermissionsChecker> {
        PermissionsChecker()
    }

    single<LocationManager> {
        get<Context>().getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    single<LocationLiveData>()
    single<WeatherLiveData>()
    single<NetworkInfoLiveData>()
    single<ManualLocationRepository>()
    single<Use24hrClockSharedPreferenceLiveData>()
    single<UseKmSharedPreferenceLiveData>()
    single<UseCelsiusSharedPreferenceLiveData>()
    single<CurrentLocationIdSharedPreferenceLiveData>()
}

val appModules = listOf(
    appModule,
    dataModule
)