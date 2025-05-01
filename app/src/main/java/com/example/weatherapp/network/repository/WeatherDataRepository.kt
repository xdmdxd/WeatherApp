package com.example.weatherapp.network.repository

import android.annotation.SuppressLint
import android.location.Geocoder
import com.example.weatherapp.data.CurrentLocation
import com.example.weatherapp.data.RemoteLocation
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
        fusedLocationProviderClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token
        ).addOnSuccessListener { location ->
            location ?: return@addOnSuccessListener onFailure()
            onSuccess(
                CurrentLocation(
                    latitude = location.latitude,
                    longitude = location.longitude
                )
            )
        }.addOnFailureListener {
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
        return geocoder.getFromLocation(latitude, longitude, 1)?.let { addresses ->
            val address = addresses[0]
            val addressText = StringBuilder()
            addressText.append(address.locality).append(", ")
            addressText.append(address.adminArea).append(", ")
            addressText.append(address.countryName)
            currentLocation.copy(
                location = addressText.toString()
            )
        } ?: currentLocation
    }
    suspend fun  searchLocation(query: String): List<RemoteLocation>? {
        val response=weatherAPI.searchLocation(query=query)
        return if (response.isSuccessful) response.body() else null
    }
}
