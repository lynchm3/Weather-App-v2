package com.marklynch.weather.activity

import android.app.Instrumentation
import android.content.Intent
import android.content.res.Resources
import android.location.Address
import android.os.Environment
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.test.InstrumentationRegistry.getTargetContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.anyIntent
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.LargeTest
import androidx.test.internal.platform.util.TestOutputEmitter.takeScreenshot
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import com.marklynch.weather.R
import com.marklynch.weather.data.WeatherDatabase
import com.marklynch.weather.data.manuallocation.ManualLocation
import com.marklynch.weather.dependencyinjection.*
import com.marklynch.weather.espressoutils.*
import com.marklynch.weather.generateGetWeatherResponse
import com.marklynch.weather.livedata.location.GpsState
import com.marklynch.weather.livedata.network.ConnectionType
import com.marklynch.weather.randomiseTestWeatherData
import com.marklynch.weather.utils.AppPermissionState
import com.marklynch.weather.utils.randomAlphaNumeric
import com.sucho.placepicker.AddressData
import com.sucho.placepicker.Constants
import com.sucho.placepicker.PlacePickerActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.mockwebserver.MockWebServer
import org.junit.*
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext
import org.koin.standalone.get
import org.koin.test.KoinTest
import java.io.File
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


@LargeTest
class ManageLocationsTest : KoinTest, KoinComponent {

    @Rule
    @JvmField
    var activityTestRule = IntentsTestRule(ManageLocationsActivity::class.java, false, false)

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
    fun checkWithNoLocations() {
        val weatherDatabase: WeatherDatabase = get()
        weatherDatabase.clearAllTables()

        activityTestRule.launchActivity(null)

        //CHeck "No locations to display" messaging shown
        assertViewDisplayed(
            R.id.tv_messaging
        )
        assertViewHasText(
            R.id.tv_messaging, activityTestRule.activity.resources.getString(
                R.string.no_locations_to_display
            )
        )

        activityTestRule.finishActivity()
    }

    @Test
    fun checkWith1Location() {
        val weatherDatabase: WeatherDatabase = get()
        weatherDatabase.clearAllTables()

        val insertLatch = CountDownLatch(1)
        val locations = listOf(
            ManualLocation(0L, "LOCATION1", 5.0, 6.0)
        )

        GlobalScope.launch {
            val manualLocationDAO = weatherDatabase.getManualLocationDao()
            manualLocationDAO.insert(locations[0])
            insertLatch.countDown()
        }

        insertLatch.await(5, TimeUnit.SECONDS)

        activityTestRule.launchActivity(null)

        //Check list is visible
        assertViewDisplayed(R.id.rv_manage_locations_list)

        //Check size of list
        assertListSize(R.id.rv_manage_locations_list, 1)

        //Click on first item
        clickItemInRecyclerView(R.id.rv_manage_locations_list, 0)

        //Check text of first item
        assertItemInRecyclerViewHasText(R.id.rv_manage_locations_list, 0, "LOCATION1")

        activityTestRule.finishActivity()
    }

    @Test
    fun checkWith2Locations() {
        val weatherDatabase: WeatherDatabase = get()
        weatherDatabase.clearAllTables()

        val insertLatch = CountDownLatch(1)
        val locations = listOf(
            ManualLocation(0L, "LOCATION1", 5.0, 6.0),
            ManualLocation(0L, "LOCATION2", 8.0, 7.0)
        )

        GlobalScope.launch {
            val manualLocationDAO = weatherDatabase.getManualLocationDao()
            manualLocationDAO.insert(locations[0])
            manualLocationDAO.insert(locations[1])
            insertLatch.countDown()
        }

        insertLatch.await(5, TimeUnit.SECONDS)

        activityTestRule.launchActivity(null)

        //check size of list
        assertListSize(R.id.rv_manage_locations_list, 2)

        //Click on first item
        clickItemInRecyclerView(R.id.rv_manage_locations_list, 0)

        //Check text of first item
        assertItemInRecyclerViewHasText(R.id.rv_manage_locations_list, 0, "LOCATION1")
//        onView(withRecyclerView(R.id.rv_manage_locations_list).atPosition(0))
//            .check(matches(hasDescendant(withText(resources.getString(R.string.rename)))))
//        onView(withRecyclerView(R.id.rv_manage_locations_list).atPosition(0))
//            .check(matches(hasDescendant(withText(resources.getString(R.string.remove)))))

        //Click on 2nd item
        clickItemInRecyclerView(R.id.rv_manage_locations_list, 1)

        //Check text of 2nd item
        assertItemInRecyclerViewHasText(R.id.rv_manage_locations_list, 1, "LOCATION2")

        activityTestRule.finishActivity()
    }


    @Test
    fun testAddLocation() {

        randomiseTestWeatherData()

        activityTestRule.launchActivity(null)

        val lat = com.marklynch.weather.testLat
        val lon = com.marklynch.weather.testLon
        val displayName = randomAlphaNumeric(5)
        val address = Address(Locale.US)
        address.subAdminArea = displayName
        val addressList: List<Address>? = listOf(address)
        val addressData = AddressData(lat, lon, addressList)

        val resultIntent = Intent()

        resultIntent.putExtra(Constants.ADDRESS_INTENT, addressData)

        intending(anyIntent()).respondWith(
            Instrumentation.ActivityResult(
                AppCompatActivity.RESULT_OK,
                resultIntent
            )
        )

        clickViewWithId(R.id.fab_add_location)
        Intents.intended(IntentMatchers.hasComponent(PlacePickerActivity::class.java.name))

        //check size of list
        assertListSize(R.id.rv_manage_locations_list, 1)

        //Click on first item
        clickItemInRecyclerView(R.id.rv_manage_locations_list, 0)

        //Check text of first item
        assertItemInRecyclerViewHasText(R.id.rv_manage_locations_list, 0, displayName)

        activityTestRule.finishActivity()
    }

    @Test
    fun testRemove() {
        val weatherDatabase: WeatherDatabase = get()
        weatherDatabase.clearAllTables()

        val insertLatch = CountDownLatch(1)
        val locations = listOf(
            ManualLocation(0L, "LOCATION1", 5.0, 6.0)
        )

        GlobalScope.launch {
            val manualLocationDAO = weatherDatabase.getManualLocationDao()
            manualLocationDAO.insert(locations[0])
            insertLatch.countDown()
        }

        insertLatch.await(5, TimeUnit.SECONDS)

        activityTestRule.launchActivity(null)

        //Check list is visible
        assertViewDisplayed(R.id.rv_manage_locations_list)

        //Check size of list
        assertListSize(R.id.rv_manage_locations_list, 1)

        //Click on first item
        clickItemInRecyclerView(R.id.rv_manage_locations_list, 0)

        //Check text of first item
        assertItemInRecyclerViewHasText(R.id.rv_manage_locations_list, 0, "LOCATION1")

        //Check remove button displayed
        assertItemInRecyclerViewHasText(
            R.id.rv_manage_locations_list,
            0,
            resources.getString(R.string.remove)
        )
        assertViewDisplayed(resources.getString(R.string.remove))

        //Click remove button
        clickViewWithText(resources.getString(R.string.remove))

        //Wait for "No Locations" message to show up
        val tvMessaging: TextView =
            activityTestRule.activity.findViewById(R.id.tv_messaging)
        val idlingResource =
            ViewVisibilityIdlingResource(tvMessaging, View.VISIBLE)
        idlingRegistry.register(idlingResource)
        idlingRegistry.unregister(idlingResource)

        //CHeck "No locations to display" messaging shown
        assertViewNotDisplayed(R.id.rv_manage_locations_list)
        assertViewDisplayed(R.id.tv_messaging)
        assertViewHasText(
            R.id.tv_messaging, activityTestRule.activity.resources.getString(
                R.string.no_locations_to_display
            )
        )

        activityTestRule.finishActivity()
    }


    @Test
    fun testRename() {
        val weatherDatabase: WeatherDatabase = get()
        weatherDatabase.clearAllTables()

        val insertLatch = CountDownLatch(1)
        val locations = listOf(
            ManualLocation(0L, "LOCATION1", 5.0, 6.0)
        )

        GlobalScope.launch {
            val manualLocationDAO = weatherDatabase.getManualLocationDao()
            manualLocationDAO.insert(locations[0])
            insertLatch.countDown()
        }

        insertLatch.await(5, TimeUnit.SECONDS)

        activityTestRule.launchActivity(null)

        //Check list is visible
        assertViewDisplayed(R.id.rv_manage_locations_list)

        //Check size of list
        assertListSize(R.id.rv_manage_locations_list, 1)

        //Click on first item
        clickItemInRecyclerView(R.id.rv_manage_locations_list, 0)

        //Check text of first item
        assertItemInRecyclerViewHasText(R.id.rv_manage_locations_list, 0, "LOCATION1")

        //Check rename button displayed
        assertItemInRecyclerViewHasText(
            R.id.rv_manage_locations_list,
            0,
            resources.getString(R.string.rename)
        )
        assertViewDisplayed(resources.getString(R.string.rename))

        //Click rename button
        clickViewWithText(resources.getString(R.string.rename))

        //Type new name
        onView(withId(R.id.edit_text)).perform(typeText("NEW LOCATION"))

        //Tap ok
        clickViewWithText("OK")

        //Check list is visible
        assertViewDisplayed(R.id.rv_manage_locations_list)

        //Check size of list
        assertListSize(R.id.rv_manage_locations_list, 1)

        //Check text of first item
        assertItemInRecyclerViewHasText(R.id.rv_manage_locations_list, 0, "NEW LOCATION")

        activityTestRule.finishActivity()
    }

    @Test
    fun testCancelAddingLocation() {
        val weatherDatabase: WeatherDatabase = get()
        weatherDatabase.clearAllTables()

        randomiseTestWeatherData()

        activityTestRule.launchActivity(null)


        intending(anyIntent()).respondWith(
            Instrumentation.ActivityResult(
                AppCompatActivity.RESULT_CANCELED,
                null
            )
        )

        clickViewWithId(R.id.fab_add_location)
        Intents.intended(IntentMatchers.hasComponent(PlacePickerActivity::class.java.name))

        //Check "no locations" showing
        assertViewNotDisplayed(R.id.rv_manage_locations_list)
        assertViewDisplayed(R.id.tv_messaging)
        assertViewHasText(
            R.id.tv_messaging, activityTestRule.activity.resources.getString(
                R.string.no_locations_to_display
            )
        )

        activityTestRule.finishActivity()

        activityTestRule.finishActivity()
    }

    @Test
    fun testCancelRenaming() {
        val weatherDatabase: WeatherDatabase = get()
        weatherDatabase.clearAllTables()

        val insertLatch = CountDownLatch(1)
        val locations = listOf(
            ManualLocation(0L, "LOCATION1", 5.0, 6.0)
        )

        GlobalScope.launch {
            val manualLocationDAO = weatherDatabase.getManualLocationDao()
            manualLocationDAO.insert(locations[0])
            insertLatch.countDown()
        }

        insertLatch.await(5, TimeUnit.SECONDS)

        activityTestRule.launchActivity(null)

        //Check list is visible
        assertViewDisplayed(R.id.rv_manage_locations_list)

        //Check size of list
        assertListSize(R.id.rv_manage_locations_list, 1)

        //Click on first item
        clickItemInRecyclerView(R.id.rv_manage_locations_list, 0)

        //Check text of first item
        assertItemInRecyclerViewHasText(R.id.rv_manage_locations_list, 0, "LOCATION1")

        //Check rename button displayed
        assertItemInRecyclerViewHasText(
            R.id.rv_manage_locations_list,
            0,
            resources.getString(R.string.rename)
        )
        assertViewDisplayed(resources.getString(R.string.rename))

        //Click rename button
        clickViewWithText(resources.getString(R.string.rename))

        //Type new name
        onView(withId(R.id.edit_text)).perform(typeText("NEW LOCATION"))

        //Tap ok
        clickViewWithText("CANCEL")

        //Check list is visible
        assertViewDisplayed(R.id.rv_manage_locations_list)

        //Check size of list
        assertListSize(R.id.rv_manage_locations_list, 1)

        //Check text of first item
        assertItemInRecyclerViewHasText(R.id.rv_manage_locations_list, 0, "LOCATION1")

        activityTestRule.finishActivity()
    }

    @Test
    fun testTryingToRenameWithoutEnteringAName() {
        val weatherDatabase: WeatherDatabase = get()
        weatherDatabase.clearAllTables()

        val insertLatch = CountDownLatch(1)
        val locations = listOf(
            ManualLocation(0L, "LOCATION1", 5.0, 6.0)
        )

        GlobalScope.launch {
            val manualLocationDAO = weatherDatabase.getManualLocationDao()
            manualLocationDAO.insert(locations[0])
            insertLatch.countDown()
        }

        insertLatch.await(5, TimeUnit.SECONDS)

        activityTestRule.launchActivity(null)

        //Check list is visible
        assertViewDisplayed(R.id.rv_manage_locations_list)

        //Check size of list
        assertListSize(R.id.rv_manage_locations_list, 1)

        //Click on first item
        clickItemInRecyclerView(R.id.rv_manage_locations_list, 0)

        //Check text of first item
        assertItemInRecyclerViewHasText(R.id.rv_manage_locations_list, 0, "LOCATION1")

        //Check rename button displayed
        assertItemInRecyclerViewHasText(
            R.id.rv_manage_locations_list,
            0,
            resources.getString(R.string.rename)
        )
        assertViewDisplayed(resources.getString(R.string.rename))

        //Click rename button
        clickViewWithText(resources.getString(R.string.rename))

        //Tap ok
        clickViewWithText("CANCEL")

        //Check list is visible
        assertViewDisplayed(R.id.rv_manage_locations_list)

        //Check size of list
        assertListSize(R.id.rv_manage_locations_list, 1)

        //Check text of first item
        assertItemInRecyclerViewHasText(R.id.rv_manage_locations_list, 0, "LOCATION1")

        activityTestRule.finishActivity()
    }
}
