package com.marklynch.weather.dependencyinjection

import com.marklynch.weather.viewmodel.MainViewModel
import com.marklynch.weather.viewmodel.ManageLocationsViewModel
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