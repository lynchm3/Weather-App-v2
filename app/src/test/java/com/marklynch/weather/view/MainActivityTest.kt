package com.marklynch.weather.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.marklynch.weather.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment.application
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowLocationManager
import org.robolectric.shadows.ShadowLog


@RunWith(RobolectricTestRunner::class)
class MainActivityTest1 {

    val sligoLatitude = 54.2766
    val sligoLongitude = 8.4761

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @Before
    @Throws(Exception::class)
    fun setUp() {
        ShadowLog.stream = System.out
    }

    @Test
    fun test1() {
        grantPermission(Manifest.permission.ACCESS_FINE_LOCATION)

        val locationManager = application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        val instanceOfLocationManager = newInstanceOf(LocationManager::class.java)
        val shadowLocationManager: ShadowLocationManager = shadowOf(locationManager)

        shadowLocationManager.setProviderEnabled(LocationManager.GPS_PROVIDER, true);
        shadowLocationManager.setProviderEnabled(LocationManager.NETWORK_PROVIDER, true);

        shadowLocationManager.setLocationEnabled(true)
        val location: Location = Location(LocationManager.GPS_PROVIDER)
        shadowLocationManager.setLastKnownLocation(LocationManager.GPS_PROVIDER, location)

        val connectivityManager: ConnectivityManager =
            application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo = connectivityManager.activeNetworkInfo
        val connected = networkInfo != null && networkInfo.isConnected

        val activityController = Robolectric.buildActivity(MainActivity::class.java)
        activityController.create().start().resume()

        val activity = activityController.get()

        val tv_raw_web_resource = activity.findViewById<TextView>(R.id.tv_raw_web_resource)
        val tv_time = activity.findViewById<TextView>(R.id.tv_time)
        val tv_location_permission = activity.findViewById<TextView>(R.id.tv_location_permission)
        val tv_gps_state = activity.findViewById<TextView>(R.id.tv_gps_state)
        val tv_location = activity.findViewById<TextView>(R.id.tv_location)
        val tv_weather = activity.findViewById<TextView>(R.id.tv_weather)
        val tv_shared_preference = activity.findViewById<TextView>(R.id.tv_shared_preference)
        val tv_connection_status = activity.findViewById<TextView>(R.id.tv_connection_status)

        Log.i("UNITTEST", "tv_raw_web_resource.text = ${tv_raw_web_resource.text}")
        Log.i("UNITTEST", "tv_time.text = ${tv_time.text}")
        Log.i("UNITTEST", "tv_location_permission.text = ${tv_location_permission.text}")
        Log.i("UNITTEST", "tv_gps_state.text = ${tv_gps_state.text}")
        Log.i("UNITTEST", "tv_location.text = ${tv_location.text}")
        Log.i("UNITTEST", "tv_weather.text = ${tv_weather.text}")
        Log.i("UNITTEST", "tv_shared_preference.text = ${tv_shared_preference.text}")
        Log.i("UNITTEST", "tv_connection_status.text = ${tv_connection_status.text}")
    }

    fun performClick(parent: View, viewId: Int) {
        parent.findViewById<FloatingActionButton>(viewId).performClick()
    }

    fun performClick(parent: Activity, viewId: Int) {
        parent.findViewById<FloatingActionButton>(viewId).performClick()
    }

    fun getAllToasts(): MutableList<Toast>? {
        return shadowOf(application).shownToasts
    }

    fun grantPermission(permission: String) {
        shadowOf(application).grantPermissions(permission)
    }

//    fun <T : Activity> verifyExpectedActivityIntent(caller: Activity, callee: Class<T>) {
//        val expectedIntent = Intent(caller, callee)
//        val actual = shadowOf(RuntimeEnvironment.application).getNextStartedActivity()
//        assertEquals(expectedIntent.getComponent(), actual.getComponent())
//    }
}