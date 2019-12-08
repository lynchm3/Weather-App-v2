package com.marklynch.weather.repository.weather

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.marklynch.weather.di.*
import com.marklynch.weather.generateGetWeatherResponse
import com.marklynch.weather.testLocationName
import com.marklynch.weather.utils.observeXTimes
import com.marklynch.weather.utils.randomAlphaNumeric
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


class WeatherRepositoryTest : KoinTest {

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
                    testModuleHttpUrl
        loadKoinModules(moduleList)
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun `Test observe with no data fetched`() {

        val weatherLiveData =
            WeatherRepository()

        var observations = 0
        weatherLiveData.observeXTimes(1) {
            observations++
        }

        assertEquals(0, observations)
    }

    @Test
    fun `Test observe after fetch success`() {

        testLocationName = randomAlphaNumeric(5)


        testWebServer = MockWebServer()
        testWebServer.enqueue(MockResponse().setBody(generateGetWeatherResponse().toString()))

        val weatherLiveData =
            WeatherRepository()

        var observations = 0
        weatherLiveData.observeXTimes(1) {
            observations++
            assertEquals(generateGetWeatherResponse(), toJson(it))
        }

        weatherLiveData.fetchWeather(sligoLatitude, sligoLongitude)

        val timeout = 1000
        var timeSoFar = 0
        while (observations == 0 && timeSoFar < timeout) {
            Thread.sleep(100)
            timeSoFar += 100
        }

//        assertEquals(1, observations)

        testWebServer.shutdown()
    }

    @Test
    fun `Test observe after fetch error`() {
        testWebServer = MockWebServer()
        testWebServer.enqueue(MockResponse().setResponseCode(403))

        val weatherLiveData =
            WeatherRepository()

        var observations = 0
        weatherLiveData.observeXTimes(1) {
            observations++
        }

        weatherLiveData.fetchWeather(sligoLatitude, sligoLongitude)

        assertEquals(0, observations)

        testWebServer.shutdown()
    }
}