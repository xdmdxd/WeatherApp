package com.example.weatherapp.depencency_injection

import com.example.weatherapp.storage.SharedPreferencesManager
import org.koin.dsl.module

val storageModule= module {
    single { SharedPreferencesManager(context = get(), gson = get()) }
}