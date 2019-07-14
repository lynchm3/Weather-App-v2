package com.marklynch.weather.livedata

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.marklynch.weather.R
import com.marklynch.weather.view.MainActivity
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.RuntimeEnvironment.application
import org.robolectric.Shadows.shadowOf


@RunWith(RobolectricTestRunner::class)
class RobolectricTest {
    @Test
    fun clickingLogin_shouldStartLoginActivity() {
        val activity = getActivity(MainActivity::class.java!!)
    }

    fun <T : Activity> getActivity(clazz:Class<T>): Activity
    {
        return Robolectric.setupActivity(clazz!!)
    }

    fun performClick(parent: View, viewId: Int)
    {
        parent.findViewById<FloatingActionButton>(viewId).performClick()
    }

    fun performClick(parent: Activity, viewId: Int)
    {
        parent.findViewById<FloatingActionButton>(viewId).performClick()
    }

    fun getAllToasts(): MutableList<Toast>? {
        return shadowOf(application).shownToasts
    }

    fun <T : Activity> verifyExpectedActivityIntent(caller:Activity, callee:Class<T>)
    {
        val expectedIntent = Intent(caller, callee)
        val actual = shadowOf(RuntimeEnvironment.application).getNextStartedActivity()
        assertEquals(expectedIntent.getComponent(), actual.getComponent())
    }
}