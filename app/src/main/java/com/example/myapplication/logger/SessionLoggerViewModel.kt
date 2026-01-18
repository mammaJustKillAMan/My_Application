package com.example.myapplication.logger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

val Context.sessionDataStore by preferencesDataStore(name = "user_sessions")

data class HikeSession(
    val startTime: String,
    val endTime: String,
    val maxAltitude: Double,
    val totalAscent: Double
)

sealed class LoggerState {
    object Idle : LoggerState()
    object Logging : LoggerState()
    object Success : LoggerState()
    data class Error(val message: String) : LoggerState()
}

class SessionLoggerViewModel(
    private val context: Context,
    private val altitudeViewModel: AltitudeViewModel
) : ViewModel() {

    private val dataStore = context.sessionDataStore

    var state by mutableStateOf<LoggerState>(LoggerState.Idle)
    var isLogging by mutableStateOf(false)

    private var startAltitude = 0.0
    private var maxAltitude = 0.0
    private var startTime: Long = 0

    // Start a new session
    fun startSession() {
        if (isLogging) return
        val currentAltitude = altitudeViewModel.state.value.currentAltitude
        startAltitude = currentAltitude
        maxAltitude = currentAltitude
        startTime = System.currentTimeMillis()
        isLogging = true
        state = LoggerState.Logging
    }

    // Stop session and save it
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
