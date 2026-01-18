package com.example.myapplication.logger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SessionLoggerViewModel(
    private val locationRepository: LocationRepository,
    private val routeRecorderViewModel: RouteRecorderViewModel
) : ViewModel() {

    var isLogging: Boolean = false
        private set

    private var locationJob: Job? = null

    fun startSession() {
        if (isLogging) return
        isLogging = true
        routeRecorderViewModel.clearRoute()

        locationJob = viewModelScope.launch {
            locationRepository.locationUpdates().collect {
                routeRecorderViewModel.addLocation(it)
            }
        }
    }

    fun stopSession() {
        if (!isLogging) return
        val endAltitude = altitudeViewModel.state.value.currentAltitude
        maxAltitude = maxOf(maxAltitude, endAltitude)
        val endTime = System.currentTimeMillis()

        val ascent = maxAltitude - startAltitude

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val session = HikeSession(
            startTime = sdf.format(Date(startTime)),
            endTime = sdf.format(Date(endTime)),
            maxAltitude = maxAltitude,
            totalAscent = ascent
        )

        viewModelScope.launch {
            try {
                // Append to previous sessions
                val key = stringPreferencesKey("sessions")
                val previous = dataStore.data.first()[key] ?: ""
                val newValue = previous +
                        "${session.startTime}," +
                        "${session.endTime}," +
                        "${session.maxAltitude}," +
                        "${session.totalAscent}\n"
                dataStore.edit { prefs ->
                    prefs[key] = newValue
                }
                isLogging = false
                state = LoggerState.Success
            } catch (e: Exception) {
                state = LoggerState.Error("Failed to save session")
            }
        }
    }
}
