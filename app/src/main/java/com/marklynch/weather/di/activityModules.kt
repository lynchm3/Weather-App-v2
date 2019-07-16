package com.marklynch.weather.di

import com.marklynch.weather.viewmodel.MainViewModel
import org.koin.android.viewmodel.experimental.builder.viewModel
import org.koin.dsl.module.module
import com.marklynch.weather.livedata.sharedpreferences.BooleanSharedPreferencesLiveData
import org.koin.experimental.builder.single

val mainModule = module {
    viewModel<MainViewModel>()
}

val booleanSharedPreferencesLiveDataModule = module {
    single<BooleanSharedPreferencesLiveData>()
}

val activityModules = listOf(mainModule)