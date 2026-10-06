package com.smartgas_frontend.shared.dashboard.data.remote

import com.smartgas_frontend.shared.dashboard.domain.model.Weather
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ExternalWeatherService {
    @GET("external/weather/current")
    suspend fun getCurrentWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ): Response<WeatherDto>
}

data class WeatherDto(
    val temperature: Double?,
    val temperatureUnit: String?,
    val relativeHumidity: Double?,
    val relativeHumidityUnit: String?,
    val windSpeed: Double?,
    val windSpeedUnit: String?,
    val source: String?
)

fun WeatherDto.toWeather() = Weather(
    temperature = temperature,
    temperatureUnit = temperatureUnit?.takeIf { it.isNotBlank() } ?: "°C",
    relativeHumidity = relativeHumidity,
    relativeHumidityUnit = relativeHumidityUnit?.takeIf { it.isNotBlank() } ?: "%",
    windSpeed = windSpeed,
    windSpeedUnit = windSpeedUnit?.takeIf { it.isNotBlank() } ?: "km/h",
    source = source?.takeIf { it.isNotBlank() } ?: "Open-Meteo"
)
