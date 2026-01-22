package com.example.myapplication.altitude

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.session.SessionStateViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private const val MIN_ALTITUDE_DELTA = 3.0      // meters
private const val MAX_REALISTIC_ASCENT = 25.0  // m/min

class AltitudeViewModel(
    private val repository: AltitudeRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AltitudeState(isLoading = true))
    val state: StateFlow<AltitudeState> = _state

    private var trackingJob: Job? = null

    fun startTracking(
        sessionStateViewModel: SessionStateViewModel,
        intervalMs: Long = 120_000L
    ) {
        if (trackingJob != null) return

        trackingJob = viewModelScope.launch {
            while (true) {
                val prevAltitude = _state.value.currentAltitude
                val altitude = repository.getCurrentAltitude() ?: prevAltitude

                val delta = altitude - prevAltitude
                val minutes = intervalMs / 60000.0

                val ascentRate = when {
                    prevAltitude == 0.0 -> 0.0
                    kotlin.math.abs(delta) < MIN_ALTITUDE_DELTA -> 0.0
                    kotlin.math.abs(delta / minutes) > MAX_REALISTIC_ASCENT -> 0.0
                    else -> delta / minutes
                }

                val risk = AltitudeRiskCalculator.calculateRisk(
                    ascentRate = ascentRate,
                    symptoms = sessionStateViewModel.symptoms
                )

                _state.value = AltitudeState(
                    currentAltitude = altitude,
                    previousAltitude = prevAltitude,
                    ascentRate = ascentRate,
                    riskLevel = risk,
                    isLoading = false
                )

                delay(intervalMs)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        trackingJob?.cancel()
    }
}
