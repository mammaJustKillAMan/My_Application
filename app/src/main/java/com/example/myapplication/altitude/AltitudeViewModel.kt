package com.example.myapplication.altitude

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.copy
import com.example.myapplication.session.SessionStateViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AltitudeState(
    val currentAltitude: Double = 0.0,
    val previousAltitude: Double = 0.0,
    val ascentRate: Double = 0.0, // meters per minute
    val isLoading: Boolean = false
)

class AltitudeViewModel(
    private val repository: AltitudeRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AltitudeState(isLoading = true))
    val state: StateFlow<AltitudeState> = _state

    private var trackingJob: Job? = null

    fun startTracking(sessionStateViewModel: SessionStateViewModel, intervalMs: Long = 120_000L) {
        if (trackingJob != null) return

        trackingJob = viewModelScope.launch {
            while (true) {
                _state.value = _state.value.copy(isLoading = true)

                val altitude = repository.getCurrentAltitude() ?: _state.value.currentAltitude
                val prevAltitude = _state.value.currentAltitude
                val ascentRate = if (prevAltitude != 0.0) {
                    (altitude - prevAltitude) / (intervalMs / 60000.0)
                } else 0.0

                // Calculate risk using SessionStateViewModel symptoms
                val risk = AltitudeRiskCalculator.calculateRisk(ascentRate, sessionStateViewModel.symptoms)

                _state.value = AltitudeState(
                    currentAltitude = altitude,
                    previousAltitude = prevAltitude,
                    ascentRate = ascentRate,
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
