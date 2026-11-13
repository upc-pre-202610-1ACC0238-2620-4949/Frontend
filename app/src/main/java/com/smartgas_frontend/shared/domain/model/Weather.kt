package com.smartgas_frontend.shared.domain.model

data class Weather(
    val temperature: Double?,
    val temperatureUnit: String,
    val relativeHumidity: Double?,
    val relativeHumidityUnit: String,
    val windSpeed: Double?,
    val windSpeedUnit: String,
    val source: String
)
