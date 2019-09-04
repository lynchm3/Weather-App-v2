package com.marklynch.weather.activities

import android.content.res.Resources
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.internal.platform.util.TestOutputEmitter.takeScreenshot
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import com.marklynch.weather.R
import com.marklynch.weather.activities.SwipeRefreshLayoutMatchers.isNotRefreshing
import com.marklynch.weather.activity.MainActivity
import com.marklynch.weather.dependencyinjection.*
import com.marklynch.weather.livedata.location.GpsState
import com.marklynch.weather.livedata.location.LocationInformation
import com.marklynch.weather.testTemperature
import com.marklynch.weather.testWindDeg
import com.marklynch.weather.testWindSpeed
import com.marklynch.weather.utils.*
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.core.AllOf.allOf
import org.junit.*
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.koin.standalone.StandAloneContext
import org.koin.test.KoinTest
import kotlin.math.roundToInt


@LargeTest
class MainAcitivityNoLocationPermissionTest : KoinTest {

    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(MainActivity::class.java, false, true)

    @JvmField
    @Rule
    val screenshotRule = ScreenshotTakingRule()

    private val resources: Resources = getInstrumentation().targetContext.resources

    private val idlingRegistry: IdlingRegistry = IdlingRegistry.getInstance()

    companion object {
        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            val moduleList = listOf(
                testModuleHttpUrl,
                testLocationLiveData
            )
            StandAloneContext.loadKoinModules(moduleList)

            testLocationInformation = LocationInformation(AppPermissionState.Denied, GpsState.Enabled, null)
            testWebServer = MockWebServer()
//            testWebServer.enqueue(MockResponse().setBody(generateGetWeatherResponse()))
            testWebServer.start()
        }

        @AfterClass
        @JvmStatic
        fun afterClass() {

//        activityTestRule.finishActivity()
            testWebServer.shutdown()
            StandAloneContext.stopKoin()


        }
    }

    @Before
    fun before() {
    }

    @After
    fun after() {
    }

    @Test
    fun testNoLocationPermission() {
//        StandAloneContext.loadKoinModules(testLocationLiveData)
        waitForLoadingToFinish()
        onView(withText(resources.getString(R.string.permission_required_body))).check(matches(isDisplayed()))



//        onView(allOf(withId(..), withEffectiveVisibility(VISIBLE))).perform(click());
//        StandAloneContext.loadKoinModules(normalLocationLiveData)
    }

    private fun waitForLoadingToFinish() {
        val pullToRefresh: SwipeRefreshLayout =
            activityTestRule.activity.findViewById(R.id.pullToRefresh)
        val idlingResource = ViewRefreshingIdlingResource(pullToRefresh, false)
        idlingRegistry.register(idlingResource)
        idlingRegistry.unregister(idlingResource)
    }

    class ScreenshotTakingRule : TestWatcher() {
        override fun failed(e: Throwable?, description: Description) {
            takeScreenshot("fail_" + System.currentTimeMillis())
        }
    }
}
