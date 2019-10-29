package com.marklynch.weather.dependencyinjection.dagger

import com.marklynch.weather.activity.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])//, ViewModelModule::class])
interface AppComponent {
    fun inject(target: MainActivity)
}