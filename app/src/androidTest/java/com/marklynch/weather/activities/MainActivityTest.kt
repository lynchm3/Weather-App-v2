package com.marklynch.weather.activities

import android.content.res.Resources
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import com.marklynch.weather.R
import com.marklynch.weather.activity.MainActivity
import com.marklynch.weather.dependencyinjection.testModuleHttpUrl
import com.marklynch.weather.dependencyinjection.testWebServer
import com.marklynch.weather.generateGetWeatherResponse
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers.containsString
import org.junit.*
import org.koin.standalone.StandAloneContext
import org.koin.test.KoinTest




@LargeTest
class MainActivityTest : KoinTest {

    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(MainActivity::class.java, false, false)

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

        println("generateGetWeatherResponse() = " + generateGetWeatherResponse())
        testWebServer = MockWebServer()
        testWebServer.enqueue(MockResponse().setBody(generateGetWeatherResponse()))
        testWebServer.start()

        activityTestRule.launchActivity(null)

//        val httpURL: HttpUrl = get {
//            parametersOf("https://api.openweathermap.org/")
//        }
//        println("checkInitialState httpURL = $httpURL")

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
                    containsString(
                        resources.getString(R.string.current_location)
                    )
                )
            )
        )

    }
}
