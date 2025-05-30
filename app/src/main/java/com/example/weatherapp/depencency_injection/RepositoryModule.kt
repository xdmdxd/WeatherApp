package com.example.weatherapp.depencency_injection

import com.example.weatherapp.network.repository.WeatherDataRepository
import org.koin.dsl.module

val repositoryModule= module {
    single { WeatherDataRepository(weatherAPI = get()) }
}