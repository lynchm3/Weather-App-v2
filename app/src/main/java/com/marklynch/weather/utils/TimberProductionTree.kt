package com.marklynch.weather.utils

import android.util.Log
import timber.log.Timber


class TimberProductionTree : Timber.Tree() {
    override fun isLoggable(priority: Int): Boolean {
        if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO)
            return false

        return true
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {

        if (!isLoggable(priority))
            return

        Log.println(priority, tag, message)
    }
}