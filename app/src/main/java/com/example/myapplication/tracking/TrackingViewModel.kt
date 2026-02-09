package com.example.myapplication.tracking

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.myapplication.altitude.RiskLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.osmdroid.util.GeoPoint

/**
 * Represents the current state of a hiking/tracking session.
 *
 * @property isTracking Indicates if tracking is currently active.
 * @property startTime The timestamp (in ms) when tracking started.
 * @property riskAtStart The [RiskLevel] at the beginning of the session.
 * @property currentPath List of [GeoPoint]s representing the route recorded so far.
 * @property durationSeconds Elapsed time of the session in seconds.
 * @property currentAltitude The latest recorded altitude in meters.
 */
data class TrackingState(
    val isTracking: Boolean = false,
    val startTime: Long = 0L,
    val riskAtStart: RiskLevel = RiskLevel.LOW,
    val currentPath: List<GeoPoint> = emptyList(),
    val durationSeconds: Long = 0L,
    val currentAltitude: Double = 0.0
)

/**
 * ViewModel responsible for managing the state of a hiking/tracking session.
 *
 * Features:
 *  - Starts and stops tracking sessions.
 *  - Records GPS location points as [GeoPoint]s.
 *  - Tracks session duration, risk level, and current altitude.
 *
 * @param application The [Application] context used for any Android-specific operations.
 */
class TrackingViewModel(application: Application) : AndroidViewModel(application) {

    // Explicitly define the type TrackingState to fix inference errors
    private val _state: MutableStateFlow<TrackingState> = MutableStateFlow(TrackingState())
    val state: StateFlow<TrackingState> = _state.asStateFlow()

    fun toggleTracking(currentRisk: RiskLevel) {
        if (_state.value.isTracking) {
            stopTracking()
        } else {
            startTracking(currentRisk)
        }
    }

    private fun startTracking(initialRisk: RiskLevel) {
        val currentTime = System.currentTimeMillis()
        _state.update { currentState ->
            currentState.copy(
                isTracking = true,
                startTime = currentTime,
                riskAtStart = initialRisk,
                currentPath = emptyList(),
                durationSeconds = 0,
                currentAltitude = 0.0
            )
        }
    }

    private fun stopTracking() {
        _state.update { currentState ->
            currentState.copy(isTracking = false)
        }
    }

    /**
     * Adds a new location point to the current tracking session.
     *
     * Updates [TrackingState.currentPath], [TrackingState.durationSeconds], and [TrackingState.currentAltitude].
     *
     * @param lat Latitude of the new point.
     * @param lon Longitude of the new point.
     * @param altitude Altitude at the new point.
     * @param risk Current [RiskLevel] at this location.
     */
    fun addLocationPoint(lat: Double, lon: Double, altitude: Double, risk: RiskLevel) {
        if (!_state.value.isTracking) return

        val newPoint = GeoPoint(lat, lon)
        val currentTime = System.currentTimeMillis()

        _state.update { currentState ->
            val duration = if (currentState.startTime > 0) {
                (currentTime - currentState.startTime) / 1000
            } else 0

            currentState.copy(
                currentPath = currentState.currentPath + newPoint,
                durationSeconds = duration,
                currentAltitude = altitude
            )
        }
    }
}