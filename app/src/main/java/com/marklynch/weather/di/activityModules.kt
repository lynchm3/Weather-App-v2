package com.marklynch.weather.di

import com.marklynch.weather.ui.viewmodel.ManageLocationsViewModel
import org.koin.android.viewmodel.experimental.builder.viewModel
import org.koin.dsl.module.module

val mainModule = module {
//    viewModel<MainViewModel>()
}

val manageLocationsModule = module {
    viewModel<ManageLocationsViewModel>()
}

val activityModules = listOf(
    mainModule,
    manageLocationsModule
)