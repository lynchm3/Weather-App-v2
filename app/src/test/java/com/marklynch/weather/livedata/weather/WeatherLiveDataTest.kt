package com.marklynch.weather.livedata.weather

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.gson.Gson
import com.marklynch.weather.di.*
import com.marklynch.weather.livedata.observeXTimes
import junit.framework.Assert.assertEquals
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.koin.standalone.StandAloneContext.stopKoin
import org.koin.test.KoinTest


class WeatherLiveDataTest : KoinTest {

    private val sligoLatitude = 54.2766
    private val sligoLongitude = -8.4761

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        val moduleList =
            appModules +
                    activityModules +
                    mockModuleApplication +
                    mockModuleHttpUrl
        loadKoinModules(moduleList)
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun `Test observe with no data fetched`() {

        val weatherLiveData =
            WeatherLiveData()

        var observations = 0
        weatherLiveData.observeXTimes(1) {
            observations++
        }

        assertEquals(0, observations)
    }

    //    @Test
    fun `Test observe after fetch success`() {

        mockWebServer = MockWebServer()
        mockWebServer?.enqueue(MockResponse().setBody(weatherResponse))

        val weatherLiveData =
            WeatherLiveData()

        var observations = 0
        weatherLiveData.observeXTimes(1) {
            observations++
            assertEquals(Gson().fromJson(weatherResponse, WeatherResponse::class.java), Gson().toJson(it))
        }

        weatherLiveData.fetchWeather(sligoLatitude, sligoLongitude)

        while (observations == 0)
            Thread.sleep(100)

        assertEquals(1, observations)

        mockWebServer?.shutdown()
    }

    ////    @Test
    fun `Test observe after fetch error`() {
        mockWebServer = MockWebServer()
        mockWebServer?.enqueue(MockResponse().setResponseCode(403))

        val weatherLiveData =
            WeatherLiveData()

        var observations = 0
        weatherLiveData.observeXTimes(1) {
            observations++
        }

        weatherLiveData.fetchWeather(sligoLatitude, sligoLongitude)

        assertEquals(0, observations)

        mockWebServer?.shutdown()
    }
}

const val weatherResponse = """{
   "coord":{
      "lon":-122.08,
      "lat":37.42
   },
   "weather":[
      {
         "id":800,
         "main":"Clear",
         "description":"clear sky",
         "icon":"01d"
      }
   ],
   "base":"stations",
   "main":{
      "temp":298.24,
      "pressure":1017,
	  "humidity":47,
	  "tempMin":294.82,
	  "tempMax":301.48
   },
   "visibility":16093,
   "wind":{
	  "speed":2.1,
	  "deg":340
   },
   "clouds":{
								"all":1
   },
   "dt":1563212122,
   "sys":{
      "type":1,
      "id":5122,
      "message":0.0134,
      "country":"US",
      "sunrise":1563195547,
      "sunset":1563247741
   },
   "timezone":-25200,
   "id":5375480,
   "name":"Mountain View",
   "cod":200
}"""