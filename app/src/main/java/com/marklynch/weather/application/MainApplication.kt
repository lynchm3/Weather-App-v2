package com.marklynch.weather.application

import android.app.Application
import android.util.Log
import com.marklynch.weather.dependencyinjection.activityModules
import com.marklynch.weather.dependencyinjection.appModules
import com.marklynch.weather.dependencyinjection.dagger.AppComponent
import com.marklynch.weather.dependencyinjection.dagger.AppModule
import com.marklynch.weather.dependencyinjection.dagger.DaggerAppComponent
import org.koin.android.ext.android.get
import org.koin.android.ext.android.startKoin
import timber.log.Timber

class MainApplication : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        startKoin(this, activityModules + appModules)

        appComponent = initDagger(this)

        Timber.plant(get())
    }

    private fun initDagger(app: MainApplication): AppComponent =
        DaggerAppComponent.builder()
            .appModule(AppModule(app))
            .build()
}