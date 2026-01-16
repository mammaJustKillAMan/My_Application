package com.example.myapplication.altitude

data class AltitudeState(
    val currentAltitude: Double,
    val previousAltitude: Double,
    val ascentRate: Double,
    val riskLevel: RiskLevel,
    val isLoading: Boolean
)