package com.example.autorization.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Coordinates(
    @Expose
    val lon: Float,
    @Expose
    val lat: Float
)

data class WeatherItem(
    @Expose
    val id: Int,
    @Expose
    val main: String,
    @Expose
    val description: String,
    @Expose
    val icon: String
)

data class WeatherParams(
    @Expose
    val temp: Float,
    @SerializedName("feels_like")
    @Expose
    val feelsLike: Float,
    @SerializedName("temp_min")
    @Expose
    val tempMin: Float,
    @SerializedName("temp_max")
    @Expose
    val tempMax: Float,
    @Expose
    val pressure: Int,
    @Expose
    val humidity: Int
)

data class WindParams(
    @Expose
    val speed: Float,
    @Expose
    val deg: Int
)

data class CloudsParams(
    @Expose
    val all: Int
)

data class SunParams(
    @Expose
    val type: Int,
    @Expose
    val id: Int,
    @Expose
    val country: String,
    @Expose
    val sunrise: Int,
    @Expose
    val sunset: Int
)

data class WeatherReport(
    @Expose
    val coord: Coordinates,
    @Expose
    val weather: Array<WeatherItem>,
    @Expose
    val base: String,
    @Expose
    val main: WeatherParams,
    @Expose
    val visibility: Int,
    @Expose
    val wind: WindParams,
    @Expose
    val clouds: CloudsParams,
    @Expose
    val dt: Int,
    @Expose
    val sys: SunParams,
    @Expose
    val timezone: Int,
    @Expose
    val id: Int,
    @Expose
    val name: String,
    @Expose
    val cod: Int
)