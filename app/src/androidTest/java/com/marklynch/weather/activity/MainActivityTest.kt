package com.marklynch.weather.activity

import android.app.Instrumentation
import android.content.Intent
import android.content.res.Resources
import android.location.Address
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.GrantPermissionRule
import com.marklynch.weather.*
import com.marklynch.weather.dependencyinjection.*
import com.marklynch.weather.espressoutils.*
import com.marklynch.weather.livedata.location.GpsState
import com.marklynch.weather.livedata.network.ConnectionType
import com.marklynch.weather.testLat
import com.marklynch.weather.testLon
import com.marklynch.weather.utils.*
import com.sucho.placepicker.AddressData
import com.sucho.placepicker.Constants
import com.sucho.placepicker.PlacePickerActivity
import okhttp3.mockwebserver.MockWebServer
import org.junit.*
import org.koin.standalone.StandAloneContext
import org.koin.test.KoinTest
import java.util.*
import kotlin.math.roundToInt


@LargeTest
class MainActivityTest : KoinTest {

    @Rule
    @JvmField
    var activityTestRule = IntentsTestRule(MainActivity::class.java, false, false)

    @JvmField
    @Rule
    val screenshotRule = ScreenshotTakingRule()

    @get:Rule
    var locationPermissionRule: GrantPermissionRule =
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

        assertViewHasText(R.id.tv_time_of_last_refresh, generateTimeString(false))
        checkWeatherInfo()
        activityTestRule.finishActivity()
    }


    @Test
    fun testFahrenheitAndCelsiusSwitch() {

        activityTestRule.launchActivity(null)
        waitForLoadingToFinish()

        //Switch to degrees C as a starting point if not there already
        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        try {
            resources.getString(R.string.action_use_celsius).click()
        } catch (e: Exception) {
            //close menu if degrees C option wasn't available
            pressBack()
        }

        //Switch to fahrenheit
        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        resources.getString(R.string.action_use_fahrenheit).click()

        //Check relevant textViews F
        assertViewHasText(
            R.id.tv_temperature,
            kelvinToFahrenheit(testTemperature).roundToInt().toString()
        )
        assertViewHasText(R.id.tv_temperature_unit, resources.getString(R.string.degreesF))

        //Switch to celsius
        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        resources.getString(R.string.action_use_celsius).click()

        //Check relevant textViews C
        assertViewHasText(
            R.id.tv_temperature,
            kelvinToCelsius(testTemperature).roundToInt().toString()
        )
        assertViewHasText(R.id.tv_temperature_unit, resources.getString(R.string.degreesC))
        activityTestRule.finishActivity()
    }


    @Test
    fun testKmAndMiSwitch() {

        activityTestRule.launchActivity(null)
        waitForLoadingToFinish()

        //Switch to km as a starting point if not there already
        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        try {
            resources.getString(R.string.action_use_km).click()
        } catch (e: Exception) {
            //close menu if use km option wasn't available
            pressBack()
        }

        //Switch to miles
        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        resources.getString(R.string.action_use_mi).click()

        //Check relevant textViews Mi
        assertViewHasText(
            R.id.tv_wind, resources.getString(
                R.string.wind_mi,
                metresPerSecondToMilesPerHour(testWindSpeed).roundToInt(),
                directionInDegreesToCardinalDirection(testWindDeg)
            )
        )

        //Switch to km
        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        resources.getString(R.string.action_use_km).click()

        //Check relevant textViews km
        assertViewHasText(
            R.id.tv_wind, resources.getString(
                R.string.wind_km,
                metresPerSecondToKmPerHour(testWindSpeed).roundToInt(),
                directionInDegreesToCardinalDirection(testWindDeg)
            )
        )
        activityTestRule.finishActivity()
    }

    @Test
    fun test12hr24hrClockSwitch() {

        activityTestRule.launchActivity(null)
        waitForLoadingToFinish()

        //Switch to 12hr clock as a starting point if not there already
        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        try {
            resources.getString(R.string.action_use_12_hr_clock).click()
        } catch (e: Exception) {
            //close menu if degrees C option wasn't available
            pressBack()
        }

        //Switch to 24hr clock
        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)

        resources.getString(R.string.action_use_24_hr_clock).click()

        //Check relevant textViews 24hr clock
        assertViewHasText(R.id.tv_time_of_last_refresh, generateTimeString(true))

        //Switch to 12hr clock
        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        resources.getString(R.string.action_use_12_hr_clock).click()

        //Check relevant textViews 12hr clock
        assertViewHasText(R.id.tv_time_of_last_refresh, generateTimeString(false))
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
        assertViewDisplayed(resources.getString(R.string.permission_required_body))
        activityTestRule.finishActivity()
    }

    @Test
    fun testLocationOff() {
        testGpsState = GpsState.Disabled
        activityTestRule.launchActivity(null)
        waitForLoadingToFinish()
        assertViewDisplayed(resources.getString(R.string.gps_required_body))
        activityTestRule.finishActivity()
    }

    @Test
    fun testNoNetwork() {
        testNetworkInfo = ConnectionType.NO_CONNECTION
        testWeatherResponse = null
        activityTestRule.launchActivity(null)
        waitForLoadingToFinish()
        assertViewDisplayed(resources.getString(R.string.no_network_body))
        activityTestRule.finishActivity()
    }

    @Test
    fun testAddLocation() {

        randomiseTestWeatherData()

        val launchActivity = activityTestRule.launchActivity(null)

        val lat = testLat
        val lon = testLon
        val displayName = randomAlphaNumeric(5)
        val address = Address(Locale.US)
        address.subAdminArea = displayName
        val addressList: List<Address>? = listOf(address)
        val addressData = AddressData(lat, lon, addressList)

        val resultIntent = Intent()

        resultIntent.putExtra(Constants.ADDRESS_INTENT, addressData)

        Intents.intending(IntentMatchers.anyIntent()).respondWith(
            Instrumentation.ActivityResult(
                AppCompatActivity.RESULT_OK,
                resultIntent
            )
        )

//        clickViewWithId(R.id.spinner_select_location)
        R.id.spinner_select_location.click()
        resources.getString(R.string.add_location_ellipses).click()
        Intents.intended(IntentMatchers.hasComponent(PlacePickerActivity::class.java.name))

        waitForLoadingToFinish()
        waitForViewToBeVisible(activityTestRule.activity.findViewById(R.id.ll_weather_info))

        checkWeatherInfo()

        activityTestRule.finishActivity()
    }

    @Test
    fun testCancelAddingLocation() {
        //TODO Select add location, then press back on add location screen rather than selecting a location
    }

    private fun waitForLoadingToFinish() {
        val pullToRefresh: SwipeRefreshLayout =
            activityTestRule.activity.findViewById(R.id.swipe_refresh_layout)
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

    private fun checkWeatherInfo() {
        assertViewHasText(
            R.id.tv_temperature,
            kelvinToCelsius(testTemperature).roundToInt().toString()
        )
        assertViewHasText(R.id.tv_temperature_unit, resources.getString(R.string.degreesC))
        assertViewHasText(R.id.tv_weather_description, testDescription)
        assertViewHasText(
            R.id.tv_humidity, resources.getString(
                R.string.humidity_percentage,
                testHumidity.roundToInt()
            )
        )
        assertViewHasText(
            R.id.tv_maximum_temperature, resources.getString(
                R.string.maximum_temperature_C,
                kelvinToCelsius(testTemperatureMax).roundToInt()
            )
        )
        assertViewHasText(
            R.id.tv_minimum_temperature, resources.getString(
                R.string.minimum_temperature_C,
                kelvinToCelsius(testTemperatureMin).roundToInt()
            )
        )
        assertViewHasText(
            R.id.tv_wind, resources.getString(
                R.string.wind_km,
                metresPerSecondToKmPerHour(testWindSpeed).roundToInt(),
                directionInDegreesToCardinalDirection(testWindDeg)
            )
        )
        assertViewHasText(
            R.id.tv_cloudiness,
            resources.getString(R.string.cloudiness_percentage, testCloudiness.roundToInt())
        )
    }
}
