package com.example.myapplication.altitude

data class AltitudeState(
    val currentAltitude: Double = 0.0,
    val previousAltitude: Double = 0.0,
    val ascentRate: Double = 0.0,
    val riskLevel: RiskLevel = RiskLevel.LOW,
    val isLoading: Boolean = false
)