package com.example.weatherapp.storage

import android.content.Context
import androidx.core.content.edit
import com.example.weatherapp.data.CurrentLocation
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferencesManager(context: Context, private val gson: Gson) {

    companion object {
        private const val PREF_NAME = "WeatherAppPref"
        private const val KEY_CURRENT_LOCATION = "currentLocation"
        private const val KEY_FAVORITE_CITIES = "favoriteCities"
    }

    private val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    // -- Current Location --

    fun saveCurrentLocation(currentLocation: CurrentLocation) {
        val currentLocationJson = gson.toJson(currentLocation)
        sharedPreferences.edit {
            putString(KEY_CURRENT_LOCATION, currentLocationJson)
        }
    }

    fun getCurrentLocation(): CurrentLocation? {
        return sharedPreferences.getString(KEY_CURRENT_LOCATION, null)?.let { json ->
            gson.fromJson(json, CurrentLocation::class.java)
        }
    }

    // -- Favorite Cities --

    fun saveFavoriteCities(favorites: Set<String>) {
        val json = gson.toJson(favorites)
        sharedPreferences.edit {
            putString(KEY_FAVORITE_CITIES, json)
        }
    }

    fun getFavoriteCities(): Set<String> {
        val json = sharedPreferences.getString(KEY_FAVORITE_CITIES, null) ?: return emptySet()
        val type = object : TypeToken<Set<String>>() {}.type
        return gson.fromJson(json, type)
    }

    fun clearFavoriteCities() {
        sharedPreferences.edit {
            remove(KEY_FAVORITE_CITIES)
        }
    }
}
