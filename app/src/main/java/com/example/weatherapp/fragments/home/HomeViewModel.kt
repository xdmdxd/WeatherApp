package com.example.weatherapp.fragments.home

import android.location.Geocoder
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.CurrentLocation
import com.example.weatherapp.data.CurrentWeather
import com.example.weatherapp.data.LiveDataEvent
import com.example.weatherapp.network.repository.WeatherDataRepository
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.launch

class HomeViewModel(private val weatherDataRepository: WeatherDataRepository) : ViewModel() {


    //region Current Location
    private val _currentLocation = MutableLiveData<CurrentLocationDataState>()
    val currentLocation: LiveData<CurrentLocationDataState> get() = _currentLocation

    fun getCurrentLocation(
        fusedLocationProviderClient: FusedLocationProviderClient,
        geocoder: Geocoder
    ){
        viewModelScope.launch {
            emitCurrentLocationUiState(isLoading = true)
            weatherDataRepository.getCurrentLocation (
                fusedLocationProviderClient=fusedLocationProviderClient,
                onSuccess = { currentLocation ->
                    updateAddressText(currentLocation,geocoder)
                },
                onFailure = {
                    emitCurrentLocationUiState(error="Unable to fetch current location")
                }
            )
        }
    }

    private fun updateAddressText(currentLocation: CurrentLocation, geocoder: Geocoder) {
        viewModelScope.launch {
            runCatching {
                weatherDataRepository.updateAddressText(currentLocation, geocoder)
            }.onSuccess { location ->
                emitCurrentLocationUiState(currentLocation = location)
            }.onFailure {
                emitCurrentLocationUiState(
                    currentLocation = currentLocation.copy(
                        location = "N/A"
                    )
                )
            }
        }
    }


    private fun emitCurrentLocationUiState(
        isLoading: Boolean = false,
        currentLocation: CurrentLocation? = null,
        error: String? = null
    ) {
        val currentLocationDataState = CurrentLocationDataState(isLoading, currentLocation, error)
        _currentLocation.value = currentLocationDataState
    }

    data class CurrentLocationDataState(
        val isLoading: Boolean,
        val currentLocation: CurrentLocation?,
        val error: String?
    )
    //endregion

    // region Weather Data
    private val _weatherData = MutableLiveData<LiveDataEvent<WeatherDataState>>()
    val weatherData: LiveData<LiveDataEvent<WeatherDataState>> get() = _weatherData

    fun getWeatherData(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            emitWeatherDataUiState(isLoading = true)

            weatherDataRepository.getWeatherData(latitude, longitude)?.let { weatherData ->
                val currentWeather = CurrentWeather(
                    icon = weatherData.current.condition.icon,
                    temperature = weatherData.current.temperature,
                    wind = weatherData.current.wind,
                    humidity = weatherData.current.humidity,
                    chanceOfRain = weatherData.forecast.forecastDay.first().day.chanceOfRain
                )

                emitWeatherDataUiState(currentWeather = currentWeather)
            } ?: emitWeatherDataUiState(error = "Unable to fetch weather data")
        }
    }


    private fun emitWeatherDataUiState(
        isLoading: Boolean = false,
        currentWeather: CurrentWeather? = null,
        error: String? = null
    ) {
        val weatherDataState = WeatherDataState(isLoading, currentWeather, error)
        _weatherData.value = LiveDataEvent(weatherDataState)
    }

    data class WeatherDataState(
        val isLoading: Boolean,
        val currentWeather: CurrentWeather?,
        val error: String?
    )
// endregion

}
