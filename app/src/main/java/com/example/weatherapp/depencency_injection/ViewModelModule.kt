package com.example.weatherapp.depencency_injection

import com.example.weatherapp.fragments.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule= module {
    viewModel{ HomeViewModel(weatherDataRepository = get()) }
    }