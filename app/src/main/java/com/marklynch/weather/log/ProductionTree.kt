package com.marklynch.weather.log

import android.util.Log
import com.marklynch.weather.BuildConfig
import timber.log.Timber

class ProductionTree : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == Log.ERROR && !BuildConfig.DEBUG) {
            //TODO
        }
    }

}