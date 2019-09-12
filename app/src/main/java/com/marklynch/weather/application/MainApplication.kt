package com.marklynch.weather.application

import android.app.Application
import com.marklynch.weather.BuildConfig
import com.marklynch.weather.dependencyinjection.activityModules
import com.marklynch.weather.dependencyinjection.appModules
import org.koin.android.ext.android.get
import org.koin.android.ext.android.startKoin
import timber.log.Timber

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val moduleList = activityModules + appModules
        startKoin(this, moduleList)


        if(BuildConfig.DEBUG) {
            val tree: Timber.Tree = get()
            Timber.plant(tree)
        }
    }
}