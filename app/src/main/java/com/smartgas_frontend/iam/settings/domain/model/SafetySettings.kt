package com.smartgas_frontend.iam.settings.domain.model

data class SafetySettings(
    val gasThreshold: Double = 50.0,
    val temperatureThreshold: Double = 45.0
)
