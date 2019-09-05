package com.marklynch.weather.dependencyinjection

import android.location.Location
import androidx.room.Room
import com.marklynch.weather.data.WeatherDatabase
import com.marklynch.weather.generateGetWeatherResponse
import com.marklynch.weather.livedata.location.GpsState
import com.marklynch.weather.livedata.location.LocationInformation
import com.marklynch.weather.livedata.location.LocationLiveData
import com.marklynch.weather.livedata.network.ConnectionType
import com.marklynch.weather.livedata.network.NetworkInfoLiveData
import com.marklynch.weather.utils.AppPermissionState
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.koin.dsl.module.module
import org.koin.experimental.builder.single


val testWeatherDatabase = module(override = true) {
    single {
        Room.inMemoryDatabaseBuilder(
            get(), WeatherDatabase::class.java
        ).build()
    }
}

//var testWebServer = MockWebServer()
lateinit var testWebServer: MockWebServer
val testModuleHttpUrl = module(override = true) {
    factory { (_: String) ->
        testWebServer.enqueue(MockResponse().setBody(generateGetWeatherResponse()))
        testWebServer.url("")
    }
}

//lateinit var testLocationInformation: LocationInformation
lateinit var testLocationPermissionState: AppPermissionState
lateinit var testGpsState: GpsState
var testLat = 53.0
var testLon = 6.0
val testLocationLiveData = module(override = true) {
    factory<LocationLiveData> {
        object : LocationLiveData() {
            override fun postValue(value: LocationInformation?) {
                super.postValue(LocationInformation(testLocationPermissionState, testGpsState, testLat, testLon))
            }
        }

    }
}

lateinit var testNetworkInfo: ConnectionType
val testNetworkInfoLiveData = module(override = true) {
    factory<NetworkInfoLiveData> {
        object : NetworkInfoLiveData(get()) {
            override fun postValue(value: ConnectionType?) {
                super.postValue(testNetworkInfo)
            }
        }

    }
}