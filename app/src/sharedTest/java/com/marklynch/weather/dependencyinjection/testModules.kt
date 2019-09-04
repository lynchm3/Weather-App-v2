package com.marklynch.weather.dependencyinjection

import androidx.room.Room
import com.marklynch.weather.data.WeatherDatabase
import com.marklynch.weather.generateGetWeatherResponse
import com.marklynch.weather.livedata.location.LocationInformation
import com.marklynch.weather.livedata.location.LocationLiveData
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

lateinit var testLocationInformation: LocationInformation
val testLocationLiveData = module(override = true) {
    single<LocationLiveData> {
        object : LocationLiveData() {
            override fun postValue(value: LocationInformation?) {
                super.postValue(testLocationInformation)
            }
        }

    }
}
val normalLocationLiveData = module(override = true) {
    single<LocationLiveData>()
}