package com.marklynch.weather.dependencyinjection.dagger

import com.marklynch.weather.application.MainApplication
import com.marklynch.weather.viewmodel.MainViewModel
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ViewModelModule {
    @Provides
    @Singleton
    fun provideMainModule(): MainViewModel = MainViewModel()
}