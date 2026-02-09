package com.example.myapplication.logger

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.myapplication.altitude.AltitudeRepository
import com.example.myapplication.altitude.RiskLevel
import com.example.myapplication.tracking.TrackingViewModel

/**
 * ViewModel responsible for logging a hiking session.
 *
 * Collects location updates from [AltitudeRepository] and sends them
 * to a [TrackingViewModel] along with the current altitude risk level.
 *
 * @property altitudeRepository Repository used to obtain location and altitude data.
 * @param application The [Application] context required by [AndroidViewModel].
 */
class SessionLoggerViewModel(
    application: Application,
    private val altitudeRepository: AltitudeRepository // Injected via factory
) : AndroidViewModel(application) {

    private var locationJob: Job? = null

    // Passing the trackingViewModel from the UI to this function
    /**
     * Starts a hiking session by collecting location updates.
     *
     * Continuously receives location data from [AltitudeRepository] every [intervalMillis]
     * and forwards it to the provided [trackingViewModel] along with the current [RiskLevel].
     *
     * If a session is already running, this function does nothing.
     *
     * @param trackingViewModel The [TrackingViewModel] to send location points to.
     * @param currentRisk The current [RiskLevel] to associate with collected points.
     */
    fun startSession(trackingViewModel: TrackingViewModel, currentRisk: RiskLevel) {
        if (locationJob?.isActive == true) return

        locationJob = viewModelScope.launch {
            altitudeRepository.getLocationUpdates(10000L).collect { location ->
                trackingViewModel.addLocationPoint(
                    location.latitude,
                    location.longitude,
                    location.altitude,
                    currentRisk
                )
            }
        }
    }

    /**
     * Stops the currently running session and cancels location collection.
     *
     * If no session is active, this function does nothing.
     */
    fun stopSession() {
        locationJob?.cancel()
    }
}