package com.marklynch.weather.application

import android.app.Application
import android.util.Log
import com.marklynch.weather.dependencyinjection.activityModules
import com.marklynch.weather.dependencyinjection.appModules
import org.koin.android.ext.android.get
import org.koin.android.ext.android.startKoin
import timber.log.Timber

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin(this, activityModules + appModules)

        Timber.plant(get())
    }
}