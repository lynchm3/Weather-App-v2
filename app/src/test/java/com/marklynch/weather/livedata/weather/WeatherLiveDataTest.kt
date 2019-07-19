package com.marklynch.weather.livedata.weather

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.marklynch.weather.di.*
import com.marklynch.weather.livedata.observeXTimes
import junit.framework.Assert.*
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.koin.standalone.StandAloneContext.stopKoin
import org.koin.standalone.get
import org.koin.test.KoinTest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


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
            //this shouldn't be triggered, weather gets triggered by valid location being available
            observations++
            assertNull(it)
        }

        assertEquals(0, observations)
    }

    @Test
    fun `Test observe after fetch success`() {


        mockWebServer = MockWebServer()
        mockWebServer?.enqueue(MockResponse().setBody(weatherResponse))

        val weatherLiveData =
            WeatherLiveData()

        var observations = 0
        var weatherResponse: WeatherResponse? = null
        weatherLiveData.observeXTimes(1) {
            observations++
            weatherResponse = it
            assertNotNull(weatherResponse)
            System.out.println("weatherResponse = " + weatherResponse)
        }

        weatherLiveData.fetchWeather(sligoLatitude,sligoLongitude)

        val maxWaitTime = 10_000L
        val waitInterval = 100L
        var waitTimeSoFar = 0L
        while (weatherResponse == null && waitTimeSoFar <= maxWaitTime) {
            Thread.sleep(waitInterval)
            waitTimeSoFar += waitInterval
        }

        assertEquals(1, observations)

        mockWebServer?.shutdown()
    }

    fun `Test observe after fetch error`() {

    }

}

val weatherResponse = """{
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
	  "temp_min":294.82,
	  "temp_max":301.48
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