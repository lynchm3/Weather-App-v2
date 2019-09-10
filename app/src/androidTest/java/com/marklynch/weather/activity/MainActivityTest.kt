package com.marklynch.weather.activity

import android.content.Intent
import android.content.res.Resources
import android.location.Address
import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.internal.platform.util.TestOutputEmitter.takeScreenshot
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.GrantPermissionRule
import com.marklynch.weather.*
import com.marklynch.weather.dependencyinjection.*
import com.marklynch.weather.espressoutils.ViewRefreshingIdlingResource
import com.marklynch.weather.espressoutils.ViewVisibilityIdlingResource
import com.marklynch.weather.espressoutils.clickView
import com.marklynch.weather.livedata.location.GpsState
import com.marklynch.weather.livedata.network.ConnectionType
import com.marklynch.weather.utils.*
import com.sucho.placepicker.AddressData
import com.sucho.placepicker.Constants
import com.sucho.placepicker.PlacePickerActivity
import okhttp3.mockwebserver.MockWebServer
import org.junit.*
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.koin.standalone.StandAloneContext
import org.koin.test.KoinTest
import java.util.*
import kotlin.math.roundToInt


@LargeTest
class MainActivityTest : KoinTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    var activityTestRule = IntentsTestRule(MainActivity::class.java, false, false)

    @JvmField
    @Rule
    val screenshotRule = ScreenshotTakingRule()

    @get:Rule
    var loctionPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)


    private val resources: Resources = getInstrumentation().targetContext.resources

    private val idlingRegistry: IdlingRegistry = IdlingRegistry.getInstance()

    companion object {
        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            val moduleList = listOf(
                testModuleHttpUrl,
                testLocationLiveData,
                testNetworkInfoLiveData,
                testWeatherLiveData,
                testWeatherDatabase
            )
            StandAloneContext.loadKoinModules(moduleList)

            testWebServer = MockWebServer()
            testWebServer.start()
        }

        @AfterClass
        @JvmStatic
        fun afterClass() {
            testWebServer.shutdown()
            StandAloneContext.stopKoin()

        }
    }

    @Before
    fun before() {
        testWeatherResponse = generateGetWeatherResponse()
        testLocationPermissionState = AppPermissionState.Granted
        testGpsState = GpsState.Enabled
        testNetworkInfo = ConnectionType.CONNECTED
    }

    @After
    fun after() {
    }

    @Test
    fun checkInitialState() {

        activityTestRule.launchActivity(null)

        waitForLoadingToFinish()

        //Spinner selected item text
//        onView(withId(R.id.spinner_select_location)).check(
//            matches(
//                withSpinnerText(
//                    resources.getString(
//                        R.string.current_location_brackets_name,
//                        testLocationName
//                    )
//                )
//            )
//        )

        onView(withId(R.id.tv_time_of_last_refresh)).check(
            matches(
                withText(
                    generateTimeString(false)
                )
            )
        )
        checkWeatherInfo()
        activityTestRule.finishActivity()
    }


    @Test
    fun testFahrenheitAndCelciusSwitch() {

        activityTestRule.launchActivity(null)
        waitForLoadingToFinish()

        //Switch to degrees C as a starting point if not there already
        Espresso.openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        try {
            clickView(resources.getString(R.string.action_use_celsius))
        } catch (e: Exception) {
            //close menu if degrees C option wasn't aavailable
            Espresso.pressBack()
        }

        //Switch to fahrenheit
        Espresso.openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        clickView(resources.getString(R.string.action_use_fahrenheit))

        //Check relevant textviews F
        onView(withId(R.id.tv_temperature)).check(
            matches(
                withText(
                    kelvinToFahrenheit(
                        testTemperature
                    ).roundToInt().toString()
                )
            )
        )
        onView(withId(R.id.tv_temperature_unit)).check(matches(withText(resources.getString(R.string.degreesF))))

        //Switch to celcius
        Espresso.openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        clickView(resources.getString(R.string.action_use_celsius))

        //Check relevant textviews C
        onView(withId(R.id.tv_temperature)).check(matches(withText(kelvinToCelsius(testTemperature).roundToInt().toString())))
        onView(withId(R.id.tv_temperature_unit)).check(matches(withText(resources.getString(R.string.degreesC))))
        activityTestRule.finishActivity()
    }


    @Test
    fun testKmAndMiSwitch() {

        activityTestRule.launchActivity(null)
        waitForLoadingToFinish()

        //Switch to km as a starting point if not there already
        Espresso.openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        try {
            clickView(resources.getString(R.string.action_use_km))
        } catch (e: Exception) {
            //close menu if degrees C option wasn't available
            Espresso.pressBack()
        }

        //Switch to miles
        Espresso.openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        clickView(resources.getString(R.string.action_use_mi))

        //Check relevant textviews Mi
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

        //Switch to km
        Espresso.openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        clickView(resources.getString(R.string.action_use_km))

        //Check relevant textviews km
        onView(withId(R.id.tv_wind)).check(
            matches(
                withText(
                    resources.getString(
                        R.string.wind_km,
                        metresPerSecondToKmPerHour(testWindSpeed).roundToInt(),
                        directionInDegreesToCardinalDirection(testWindDeg)
                    )
                )
            )
        )
        activityTestRule.finishActivity()
    }

    @Test
    fun test12hr24hrClockSwitch() {

        activityTestRule.launchActivity(null)
        waitForLoadingToFinish()

        //Switch to 12hr clock as a starting point if not there already
        Espresso.openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        try {
            clickView(resources.getString(R.string.action_use_12_hr_clock))
        } catch (e: Exception) {
            //close menu if degrees C option wasn't available
            Espresso.pressBack()
        }

        //Switch to 24hr clock
        Espresso.openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)

        clickView(resources.getString(R.string.action_use_24_hr_clock))

        //Check relevant textviews 24hr clcok
        onView(withId(R.id.tv_time_of_last_refresh)).check(
            matches(
                withText(
                    generateTimeString(true)
                )
            )
        )

        //Switch to 12hr clock
        Espresso.openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        clickView(resources.getString(R.string.action_use_12_hr_clock))

        //Check relevant textviews 12hr clock
        onView(withId(R.id.tv_time_of_last_refresh)).check(
            matches(
                withText(
                    generateTimeString(false)
                )
            )
        )
        activityTestRule.finishActivity()
    }

    @Test
    fun testNoLocationPermission() {
        val moduleList = listOf(
            testLocationLiveData
        )
        StandAloneContext.loadKoinModules(moduleList)

        testLocationPermissionState = AppPermissionState.Denied

        activityTestRule.launchActivity(null)
        waitForLoadingToFinish()
        onView(withText(resources.getString(R.string.permission_required_body))).check(
            matches(
                isDisplayed()
            )
        )
        activityTestRule.finishActivity()
    }

    @Test
    fun testLocationOff() {
        testGpsState = GpsState.Disabled
        activityTestRule.launchActivity(null)
        waitForLoadingToFinish()
        onView(withText(resources.getString(R.string.gps_required_body))).check(matches(isDisplayed()))
        activityTestRule.finishActivity()
    }

    @Test
    fun testNoNetwork() {
        testNetworkInfo = ConnectionType.NO_CONNECTION
        testWeatherResponse = null
        activityTestRule.launchActivity(null)
        waitForLoadingToFinish()
        onView(withText(resources.getString(R.string.no_network_body))).check(matches(isDisplayed()))
        activityTestRule.finishActivity()
    }

    @Test
    fun testAddLocation() {

        randomiseTestWeatherData()

        activityTestRule.launchActivity(null)
        waitForLoadingToFinish()
        waitForViewToBeVisible(activityTestRule.activity.findViewById(R.id.ll_weather_info))

        Thread.sleep(3_000)

        randomiseTestWeatherData()

        var lat = com.marklynch.weather.testLat
        var lon = com.marklynch.weather.testLon
        val displayName = randomAlphaNumeric(5)
        val address = Address(Locale.US)
        address.adminArea = displayName
        var addressList: List<Address>? = listOf(address)
        val addressData = AddressData(lat, lon, addressList)

        val resultIntent = Intent()

        resultIntent.putExtra(Constants.ADDRESS_INTENT, addressData)


//        intending(anyIntent()).respondWith(
//            ActivityResult(
//                AppCompatActivity.RESULT_OK,
//                resultIntent
//            )
//        )

        clickView(R.id.spinner_select_location)
        clickView(resources.getString(R.string.add_location_ellipses))
        Intents.intended(IntentMatchers.hasComponent(PlacePickerActivity::class.java.name))

        Thread.sleep(3_000)
        clickView(com.sucho.placepicker.R.id.place_chosen_button)

        Thread.sleep(5_000)

        waitForLoadingToFinish()
        waitForViewToBeVisible(activityTestRule.activity.findViewById(R.id.ll_weather_info))
        Thread.sleep(5_000)
        checkWeatherInfo()

        activityTestRule.finishActivity()
    }

    @Test
    fun testCancelAddingLocation() {
        //TODO Select add location, then press back on add lcoation screen rather than selecting a location
    }

    private fun waitForLoadingToFinish() {
        val pullToRefresh: SwipeRefreshLayout =
            activityTestRule.activity.findViewById(R.id.pull_to_refresh)
        val idlingResource =
            ViewRefreshingIdlingResource(pullToRefresh, false)
        idlingRegistry.register(idlingResource)
        idlingRegistry.unregister(idlingResource)
    }

    private fun waitForViewToBeVisible(view: View) {
        val idlingResource =
            ViewVisibilityIdlingResource(view, View.VISIBLE)
        idlingRegistry.register(idlingResource)
        idlingRegistry.unregister(idlingResource)
    }

    class ScreenshotTakingRule : TestWatcher() {
        override fun failed(e: Throwable?, description: Description) {
            takeScreenshot("fail_" + System.currentTimeMillis())
        }
    }

    private fun checkWeatherInfo() {
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
                        R.string.wind_km,
                        metresPerSecondToKmPerHour(testWindSpeed).roundToInt(),
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
}
