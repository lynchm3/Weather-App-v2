package com.marklynch.weather.di.dagger

import com.marklynch.weather.ui.viewmodel.MainViewModel
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ViewModelModule {
    @Provides
    @Singleton
    fun provideMainModule(): MainViewModel = MainViewModel()
}