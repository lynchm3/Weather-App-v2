package com.marklynch.weather.activities
import android.provider.Settings.Global.getString
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import com.marklynch.weather.activity.MainActivity
import org.junit.Rule
import org.junit.Test
import androidx.test.rule.ActivityTestRule
import com.marklynch.weather.R
import org.hamcrest.CoreMatchers.containsString


@LargeTest
class MainActivityTest {
    
    @Rule @JvmField var activityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun checkInitialState() {
        onView(withId(R.id.spinner_select_location)).check(matches(withSpinnerText(containsString("Current Location"))))
//        onView(withId(R.id.spinner_select_location))
    }
}