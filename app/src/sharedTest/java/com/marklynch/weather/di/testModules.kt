package com.marklynch.weather.di

import androidx.room.Room
import com.marklynch.weather.data.WeatherDatabase
import com.marklynch.weather.generateGetForecastResponse
import com.marklynch.weather.repository.location.CurrentLocationInformation
import com.marklynch.weather.repository.location.GpsState
import com.marklynch.weather.repository.location.LocationRepository
import com.marklynch.weather.repository.network.ConnectionType
import com.marklynch.weather.repository.network.NetworkInfoLiveData
import com.marklynch.weather.utils.AppPermissionState
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.koin.dsl.module.module


val testWeatherDatabase = module(override = true) {
    single {
        Room.inMemoryDatabaseBuilder(
            get(), WeatherDatabase::class.java
        ).allowMainThreadQueries().build()
    }
}

//var testWebServer = MockWebServer()
lateinit var testWebServer: MockWebServer
val testModuleHttpUrl = module(override = true) {
    factory { (_: String) ->
        testWebServer.enqueue(MockResponse().setBody(generateGetForecastResponse().toString()))
    }
}


//var testWeatherResponse: ForecastResponse? = null
//val testWeatherLiveData = module(override = true) {
//    factory<WeatherRepository> {
//        object : WeatherRepository() {
//            override fun postValue(value: ForecastResponse?) {
//                super.postValue(testWeatherResponse)
//            }
//        }
//    }
//}

lateinit var testLocationPermissionState: AppPermissionState
lateinit var testGpsState: GpsState
var testLat = 53.0
var testLon = 6.0
val testLocationLiveData = module(override = true) {
    factory<LocationRepository> {
        object : LocationRepository() {
            override fun postValue(value: CurrentLocationInformation?) {
                super.postValue(
                    CurrentLocationInformation(
                        testLocationPermissionState,
                        testGpsState,
                        testLat,
                        testLon
                    )
                )
            }
        }

    }
}

lateinit var testNetworkInfo: ConnectionType
val testNetworkInfoLiveData = module(override = true) {
    factory<NetworkInfoLiveData> {
        object : NetworkInfoLiveData() {
            override fun postValue(value: ConnectionType?) {
                super.postValue(testNetworkInfo)
            }
        }
    }
}