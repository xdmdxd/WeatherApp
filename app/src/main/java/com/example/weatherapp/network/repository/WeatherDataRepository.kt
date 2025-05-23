package com.example.weatherapp.network.repository

import android.annotation.SuppressLint
import android.location.Geocoder
import android.util.Log
import com.example.weatherapp.data.CurrentLocation
import com.example.weatherapp.data.RemoteLocation
import com.example.weatherapp.data.RemoteWeatherData
import com.example.weatherapp.network.api.WeatherAPI
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import retrofit2.http.Query
import java.io.IOException


class WeatherDataRepository(private val weatherAPI: WeatherAPI) {

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(
        fusedLocationProviderClient: FusedLocationProviderClient,
        onSuccess: (currentLocation: CurrentLocation) -> Unit,
        onFailure: () -> Unit
    ) {
        android.util.Log.d("LocationDebug", "Requesting current location...")

        fusedLocationProviderClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token
        ).addOnSuccessListener { location ->
            if (location == null) {
                android.util.Log.e("LocationDebug", "Location is null!")
                onFailure()
                return@addOnSuccessListener
            }

            val latitude = location.latitude
            val longitude = location.longitude
            android.util.Log.d("LocationDebug", "Lat: $latitude, Lon: $longitude")

            val currentLocation = CurrentLocation(
                latitude = latitude,
                longitude = longitude
            )

            onSuccess(currentLocation)

        }.addOnFailureListener { exception ->
            android.util.Log.e("LocationDebug", "Failed to get location: ${exception.message}")
            onFailure()
        }
    }


    @Suppress("DEPRECATION")
    fun updateAddressText(
        currentLocation: CurrentLocation,
        geocoder: Geocoder
    ): CurrentLocation {
        val latitude = currentLocation.latitude ?: return currentLocation
        val longitude = currentLocation.longitude ?: return currentLocation

        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        val address = addresses?.firstOrNull() ?: return currentLocation

        val city = address.locality
            ?: address.subAdminArea
            ?: address.subLocality
            ?: address.featureName
            ?: "Neznámé město"

        val region = address.adminArea ?: ""
        val country = address.countryName ?: ""

        val addressText = listOf(city, region, country)
            .filter { it.isNotBlank() }
            .joinToString(", ")

        return currentLocation.copy(location = addressText)
    }


    suspend fun searchLocation(query: String): List<RemoteLocation>? {
        return try {
            val response = weatherAPI.searchLocation(query = query)

            if (response.isSuccessful) {
                val body = response.body()
                android.util.Log.d("APIDebug", "Success! Found ${body?.size ?: 0} results: $body")
                body
            } else {
                android.util.Log.e("APIDebug", "Error ${response.code()}: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            android.util.Log.e("APIDebug", "Exception during search: ${e.message}")
            null
        }
    }

    suspend fun getWeatherData(latitude: Double, longitude: Double): RemoteWeatherData? {
        val response = weatherAPI.getWeatherData(query = "$latitude,$longitude")
        return if (response.isSuccessful) response.body() else null
    }


}
