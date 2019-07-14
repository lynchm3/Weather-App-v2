package com.marklynch.weather.view

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.location.LocationManager
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.marklynch.weather.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowApplication
import org.robolectric.shadows.ShadowLocationManager
import org.robolectric.shadows.ShadowLog


@RunWith(RobolectricTestRunner::class)
class MainActivityTest {


    val sligoLatitude = 54.2766
    val sligoLongitude = 8.4761

    val application: Application = ApplicationProvider.getApplicationContext()
    val shadowApplication: ShadowApplication = shadowOf(application)

    val locationManager = application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val shadowLocationManager: ShadowLocationManager = shadowOf(locationManager)

    val activityController = Robolectric.buildActivity(MainActivity::class.java)

    val activity = activityController.get()

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @Before
    @Throws(Exception::class)
    fun setUp() {
        ShadowLog.stream = System.out
        activityController.create().start().resume()
    }

    @Test
    fun test1() {
        shadowApplication.grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION)

        val tv_raw_web_resource = activity.findViewById<TextView>(R.id.tv_raw_web_resource)
        val tv_time = activity.findViewById<TextView>(R.id.tv_time)
        val tv_location = activity.findViewById<TextView>(R.id.tv_location)
        val tv_weather = activity.findViewById<TextView>(R.id.tv_weather)
        val tv_shared_preference = activity.findViewById<TextView>(R.id.tv_shared_preference)
        val tv_connection_status = activity.findViewById<TextView>(R.id.tv_connection_status)

        Thread.sleep(1_000)

        Log.i("UNITTEST", "tv_raw_web_resource.text = ${tv_raw_web_resource.text}")
        Log.i("UNITTEST", "tv_time.text = ${tv_time.text}")
        Log.i("UNITTEST", "tv_location.text = ${tv_location.text}")
        Log.i("UNITTEST", "tv_weather.text = ${tv_weather.text}")
        Log.i("UNITTEST", "tv_shared_preference.text = ${tv_shared_preference.text}")
        Log.i("UNITTEST", "tv_connection_status.text = ${tv_connection_status.text}")
    }

    @Test
    fun `Test denying fine location permission`() {
        shadowApplication.denyPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
        val tvLocation = activity.findViewById<TextView>(R.id.tv_location)
        Log.i("UNITTEST", "tv_location.text = ${tvLocation.text}")
        assertEquals(application.getString(R.string.fine_location_permission_denied), tvLocation.text)
    }

    @Test
    fun `Test granting fine location permission`() {
        shadowApplication.grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
        val tvLocation = activity.findViewById<TextView>(R.id.tv_location)
        Log.i("UNITTEST", "tv_location.text = ${tvLocation.text}")
        assertNotEquals(application.getString(R.string.fine_location_permission_denied), tvLocation.text)
    }

    @Test
    fun `Test turning off GPS`() {
        shadowApplication.grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
        shadowApplication.grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
        shadowLocationManager.setProviderEnabled(LocationManager.GPS_PROVIDER, true)
        shadowLocationManager.setProviderEnabled(LocationManager.NETWORK_PROVIDER, true)
        shadowLocationManager.setLocationEnabled(true)
        val tvLocation = activity.findViewById<TextView>(R.id.tv_location)
        Log.i("UNITTEST", "tv_location.text = ${tvLocation.text}")
        assertEquals(application.getString(R.string.location_setting_turned_off), tvLocation.text)
    }

    fun performClick(parent: View, viewId: Int) {
        parent.findViewById<FloatingActionButton>(viewId).performClick()
    }

    fun performClick(parent: Activity, viewId: Int) {
        parent.findViewById<FloatingActionButton>(viewId).performClick()
    }

//    fun getAllToasts(): MutableList<Toast>? {
//        return shadowOf(application).shownToasts
//    }

//    fun <T : Activity> verifyExpectedActivityIntent(caller: Activity, callee: Class<T>) {
//        val expectedIntent = Intent(caller, callee)
//        val actual = shadowOf(RuntimeEnvironment.application).getNextStartedActivity()
//        assertEquals(expectedIntent.getComponent(), actual.getComponent())
//    }
}