package com.example.autorization.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val APPID = "0e99cb8ea13ad7133b2afffb6e38232c"

private val mInstance: NetworkService = NetworkService()

fun getNetworkService() : NetworkService {
    return mInstance
}

class NetworkService {
    private val BASE_URL = "https://api.openweathermap.org/"
    private var mRetrofit: Retrofit? = null

    init {
        mRetrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getWeatherApi(): WeatherApi? {
        return mRetrofit?.create<WeatherApi>(WeatherApi::class.java)
    }
}