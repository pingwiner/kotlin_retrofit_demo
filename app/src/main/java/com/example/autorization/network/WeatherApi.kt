package com.example.autorization.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherApi {

    @GET("data/2.5/weather?")
    fun getWeatherForCity(@Query("q") city: String, @Query("APPID") apiKey: String): Call<WeatherReport?>?
}