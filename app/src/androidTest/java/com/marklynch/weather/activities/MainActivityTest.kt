package com.marklynch.weather.activities

import android.content.res.Resources
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.internal.platform.util.TestOutputEmitter.takeScreenshot
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import com.marklynch.weather.*
import com.marklynch.weather.activity.MainActivity
import com.marklynch.weather.dependencyinjection.testModuleHttpUrl
import com.marklynch.weather.dependencyinjection.testWebServer
import com.marklynch.weather.utils.directionInDegreesToCardinalDirection
import com.marklynch.weather.utils.kelvinToCelsius
import com.marklynch.weather.utils.metresPerSecondToMilesPerHour
import com.marklynch.weather.utils.randomAlphaNumeric
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.*
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.koin.standalone.StandAloneContext
import org.koin.test.KoinTest
import kotlin.math.roundToInt
import kotlin.random.Random


@LargeTest
class MainActivityTest : KoinTest {

    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(MainActivity::class.java, false, false)

    @JvmField
    @Rule
    val screenshotRule = ScreenshotTakingRule()

    private val resources: Resources = getInstrumentation().targetContext.resources

    companion object {
        @BeforeClass
        @JvmStatic
        fun beforeClass() {
        }
    }

    @Before
    fun before() {
        val moduleList = listOf(
            testModuleHttpUrl
        )
        StandAloneContext.loadKoinModules(moduleList)
    }

    @After
    fun after() {

    }

    @Test
    fun checkInitialState() {
        testLocationName = randomAlphaNumeric(5)
        testDescription = randomAlphaNumeric(5)
        testTemperature = Random.nextDouble()
        testHumidity = Random.nextDouble()
        testTemperatureMin = Random.nextDouble()
        testTemperatureMax = Random.nextDouble()
        testWindSpeed = Random.nextDouble()
        testWindDeg = Random.nextDouble()
        testCloudiness = Random.nextDouble()

        testWebServer = MockWebServer()
        testWebServer.enqueue(MockResponse().setBody(generateGetWeatherResponse()))
        testWebServer.start()

        activityTestRule.launchActivity(null)

        val idlingRegistry: IdlingRegistry = IdlingRegistry.getInstance()

        //Wait for weather view to be visible
        val llWeatherInfo: LinearLayoutCompat =
            activityTestRule.activity.findViewById(R.id.ll_weather_info)
        idlingRegistry.register(ViewVisibilityIdlingResource(llWeatherInfo, View.VISIBLE))
        onView(withId(R.id.ll_weather_info)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        //Spinner selected item text
        onView(withId(R.id.spinner_select_location)).check(
            matches(
                withSpinnerText(
                    resources.getString(
                        R.string.current_location_brackets_name,
                        testLocationName
                    )
                )
            )
        )

        //Check weather info loaded from mockserver displayed
        onView(withId(R.id.tv_temperature)).check(matches(withText(kelvinToCelsius(testTemperature).roundToInt().toString())))
        onView(withId(R.id.tv_temperature_unit)).check(matches(withText(resources.getString(R.string.degreesC))))
        onView(withId(R.id.tv_weather_description)).check(matches(withText(testDescription)))
        onView(withId(R.id.tv_humidity)).check(
            matches(
                withText(
                    resources.getString(
                        R.string.humidity_percentage,
                        testHumidity.roundToInt()
                    )
                )
            )
        )
        onView(withId(R.id.tv_maximum_temperature)).check(
            matches(
                withText(
                    resources.getString(
                        R.string.maximum_temperature_C,
                        kelvinToCelsius(testTemperatureMax).roundToInt()
                    )
                )
            )
        )
        onView(withId(R.id.tv_minimum_temperature)).check(
            matches(
                withText(
                    resources.getString(
                        R.string.minimum_temperature_C,
                        kelvinToCelsius(testTemperatureMin).roundToInt()
                    )
                )
            )
        )
        onView(withId(R.id.tv_wind)).check(
            matches(
                withText(
                    resources.getString(
                        R.string.wind_mi,
                        metresPerSecondToMilesPerHour(testWindSpeed).roundToInt(),
                        directionInDegreesToCardinalDirection(testWindDeg)
                    )
                )
            )
        )
        onView(withId(R.id.tv_cloudiness)).check(
            matches(
                withText(
                    resources.getString(R.string.cloudiness_percentage, testCloudiness.roundToInt())
                )
            )
        )
    }

    class ScreenshotTakingRule : TestWatcher() {
        override fun failed(e: Throwable?, description: Description) {
            takeScreenshot("fail_" + System.currentTimeMillis())
        }
    }
}
