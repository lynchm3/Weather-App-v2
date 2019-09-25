package com.marklynch.weather.utils

import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import timber.log.Timber


class TimberProductionTree : Timber.Tree(), KoinComponent {
    override fun isLoggable(priority: Int): Boolean {
        if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO)
            return false

        return true
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {

        if (!isLoggable(priority))
            return

        Log.println(priority, tag, message)

        if(priority == Log.ASSERT) {
            val params = Bundle()
            params.putString("message", message)
            params.putString("exception", t.toString())
            get<FirebaseAnalytics>().logEvent("assert_fail", params)
        }
    }
}