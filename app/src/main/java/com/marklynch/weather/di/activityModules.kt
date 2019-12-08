package com.marklynch.weather.di

import com.marklynch.weather.ui.viewmodel.MainViewModel
import org.koin.android.viewmodel.experimental.builder.viewModel
import org.koin.dsl.module.module

val mainModule = module {
    viewModel<MainViewModel>()
}

val activityModules = listOf(
    mainModule
)