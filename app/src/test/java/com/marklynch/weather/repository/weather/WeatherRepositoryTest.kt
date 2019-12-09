package com.marklynch.weather.repository.weather

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.marklynch.weather.di.*
import com.marklynch.weather.generateGetForecastResponse
import com.marklynch.weather.model.response.ForecastData
import com.marklynch.weather.model.response.ForecastResponse
import com.marklynch.weather.model.response.Weather
import com.marklynch.weather.model.response.WeatherInfo
import com.marklynch.weather.utils.observeXTimes
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
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


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
    fun `Test observe after fetch success`() {

        val weatherRepository = WeatherRepository()

        testWebServer = MockWebServer()
        testWebServer.start()
        testWebServer.enqueue(MockResponse().setBody(generateGetForecastResponse().toString()))

        val weatherLiveData =
            weatherRepository.liveData

        val countDownLatch = CountDownLatch(1)

        val expected = ForecastResponse(listOf(ForecastData("2014-07-23 09:00:00",
            WeatherInfo(298.77), listOf(Weather("overcast clouds", "04d")))))

        weatherLiveData.observeXTimes(1) {
            countDownLatch.countDown()
            assertEquals("Forecast response not as expected",generateGetForecastResponse(), expected)
        }

        weatherRepository.fetchWeather(sligoLatitude, sligoLongitude)

        countDownLatch.await(2, TimeUnit.SECONDS)

        assertEquals("livedata not observed", 0, countDownLatch.count)

        testWebServer.shutdown()
    }

    @Test
    fun `Test observe after fetch error`() {
        testWebServer = MockWebServer()
        testWebServer.enqueue(MockResponse().setResponseCode(403))

        val weatherRepository =
            WeatherRepository()

        val countDownLatch = CountDownLatch(1)
        weatherRepository.liveData.observeXTimes(1) {
            countDownLatch.countDown()
        }

        weatherRepository.fetchWeather(sligoLatitude, sligoLongitude)

        countDownLatch.await(2, TimeUnit.SECONDS)

        assertEquals(1, countDownLatch.count)

        testWebServer.shutdown()
    }

    @Test
    fun `Test observe with no data fetched`() {

        val weatherRepository =
            WeatherRepository()

        val countDownLatch = CountDownLatch(1)
        weatherRepository.liveData.observeXTimes(1) {
            countDownLatch.countDown()
        }

        countDownLatch.await(2, TimeUnit.SECONDS)

        assertEquals(1, countDownLatch.count)
    }

}