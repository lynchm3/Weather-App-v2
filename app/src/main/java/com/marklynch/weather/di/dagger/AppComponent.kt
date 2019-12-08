package com.marklynch.weather.di.dagger

import com.marklynch.weather.ui.activity.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
//@Component(modules = [AppModule::class])
@Component(modules = [AppModule::class, ViewModelModule::class])
interface AppComponent {
    fun inject(target: MainActivity)
}