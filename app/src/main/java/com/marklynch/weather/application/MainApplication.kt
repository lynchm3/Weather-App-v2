package com.marklynch.weather.application

import android.app.Application
import com.marklynch.weather.di.activityModules
import com.marklynch.weather.di.appModules
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