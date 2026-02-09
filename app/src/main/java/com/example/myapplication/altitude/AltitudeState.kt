package com.example.myapplication.altitude

/**
 * Represents the current altitude-related state of the user.
 *
 * This state is typically produced by combining location updates
 * with risk calculations and is suitable for use in reactive UI
 * layers (e.g. ViewModel / Compose).
 *
 * @property currentAltitude Current altitude in meters.
 * @property previousAltitude Previously recorded altitude in meters.
 * Used to calculate ascent or descent rate.
 * @property ascentRate Vertical speed in meters per minute.
 * Positive values indicate ascent; negative values indicate descent.
 * @property riskLevel Calculated altitude sickness risk level.
 * @property isLoading Indicates whether altitude data is currently being loaded.
 */
data class AltitudeState(
    val currentAltitude: Double = 0.0,
    val previousAltitude: Double = 0.0,
    val ascentRate: Double = 0.0,
    val riskLevel: RiskLevel = RiskLevel.LOW,
    val isLoading: Boolean = false
)