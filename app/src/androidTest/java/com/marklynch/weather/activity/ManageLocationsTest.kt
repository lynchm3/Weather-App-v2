package com.marklynch.weather.activity

import android.content.res.Resources
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.LargeTest
import androidx.test.internal.platform.util.TestOutputEmitter.takeScreenshot
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.GrantPermissionRule
import com.marklynch.weather.R
import com.marklynch.weather.data.WeatherDatabase
import com.marklynch.weather.data.manuallocation.ManualLocation
import com.marklynch.weather.dependencyinjection.*
import com.marklynch.weather.espressoutils.withListSize
import com.marklynch.weather.espressoutils.withViewAtPosition
import com.marklynch.weather.generateGetWeatherResponse
import com.marklynch.weather.livedata.location.GpsState
import com.marklynch.weather.livedata.network.ConnectionType
import com.marklynch.weather.utils.AppPermissionState
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.Matchers
import org.junit.*
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext
import org.koin.standalone.get
import org.koin.test.KoinTest
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


@LargeTest
class ManageLocationsTest : KoinTest, KoinComponent {

//    @get:Rule
//    val rule = InstantTaskExecutorRule()

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
        onView(withId(R.id.rv_manage_locations_list)).check(ViewAssertions.matches(withListSize(2)));

        //Click on first item
        onView(withId(R.id.rv_manage_locations_list))
            .inRoot(withDecorView(`is`(activityTestRule.activity.window.decorView)))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        //Check view at 0 is correct layout
        onView(withId(R.id.rv_manage_locations_list))
            .inRoot(withDecorView(`is`(activityTestRule.activity.window.decorView))).check(
                matches(
                    withViewAtPosition(
                        0,
                        Matchers.allOf(
                            withId(R.layout.list_item_manage_locations),
                            isDisplayed()
                        )
                    )
                )
            )

//        onView(withId(R.id.rv_manage_locations_list))
//            .inRoot(
//                withDecorView(
//                    Matchers.`is`(activityTestRule.activity.window.decorView)
//                )
//            )
//            .check(
//                matches(
//                    withViewAtPosition(
//                        1, Matchers.allOf(
//                            withId(R.layout.list_item_manage_locations), isDisplayed()
//                        )
//                    )
//                )
//            )


//        onData(
//            allOf(
//                `is`(instanceOf(List::class.java)),
//                hasEntry(equalTo("displayName"), `is`("LOCATION1"))
//            )
//        )
//            .perform(click())
//
//        ^^^doesn't work because recyclerview is not an adapterview


        Thread.sleep(100_000)

        activityTestRule.finishActivity()
    }

    class ScreenshotTakingRule : TestWatcher() {
        override fun failed(e: Throwable?, description: Description) {
            takeScreenshot("fail_" + System.currentTimeMillis())
        }
    }
}
