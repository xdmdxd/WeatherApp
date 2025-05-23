package com.example.weatherapp.network.api

import com.example.weatherapp.data.RemoteLocation
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {
    companion object{
        const val BASE_URL="https://api.weatherapi.com/v1/"
        const val API_KEY="b90a80b05ec44bf5aff153140250105"
    }
    @GET("search.json")
    suspend fun searchLocation(
        @Query("key") key: String = API_KEY,
        @Query("q") query: String
    ): Response<List<RemoteLocation>>

}