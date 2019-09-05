package com.marklynch.weather.activity

import android.content.res.Resources
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.filters.LargeTest
import androidx.test.internal.platform.util.TestOutputEmitter.takeScreenshot
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import com.marklynch.weather.R
import com.marklynch.weather.dependencyinjection.testLocationInformation
import com.marklynch.weather.dependencyinjection.testLocationLiveData
import com.marklynch.weather.dependencyinjection.testModuleHttpUrl
import com.marklynch.weather.dependencyinjection.testWebServer
import com.marklynch.weather.livedata.location.GpsState
import com.marklynch.weather.livedata.location.LocationInformation
import com.marklynch.weather.utils.AppPermissionState
import okhttp3.mockwebserver.MockWebServer
import org.junit.*
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.koin.standalone.StandAloneContext
import org.koin.test.KoinTest


@LargeTest
class MainAcitivityFailureTests : KoinTest {

    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(MainActivity::class.java, false, false)

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
            testWebServer = MockWebServer()
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
        testLocationInformation =
            LocationInformation(AppPermissionState.Denied, GpsState.Enabled, null)
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
        testLocationInformation =
            LocationInformation(AppPermissionState.Granted, GpsState.Disabled, null)
        activityTestRule.launchActivity(null)
        waitForLoadingToFinish()
        onView(withText(resources.getString(R.string.gps_required_body))).check(matches(isDisplayed()))
        activityTestRule.finishActivity()
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
