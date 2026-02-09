package com.example.myapplication.altitude

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.NotificationHelper
import com.google.android.gms.location.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for tracking altitude changes and evaluating risk.
 *
 * Combines GPS altitude updates with symptom input to compute ascent rate
 * and altitude sickness risk. Emits immutable [AltitudeState] updates for
 * UI observation and triggers notifications when risk levels change.
 *
 * @param application Application context used for system services
 * and notifications.
 */
class AltitudeViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * Helper responsible for displaying user notifications related to altitude risk.
     */
    private val notificationHelper = NotificationHelper(application)

    /**
     * Internal mutable state holding the current altitude tracking data.
     */
    private val _state = MutableStateFlow(AltitudeState())
    /**
     * Public, read-only state flow exposed to the UI.
     */
    val state = _state.asStateFlow()

    /**
     * Google Play Services fused location client used to receive altitude updates.
     */
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)
    /**
     * Callback receiving continuous location updates.
     * Initialized when tracking starts and cleared on stop.
     */
    private var locationCallback: LocationCallback? = null

    /**
     * Last recorded altitude value, used to calculate ascent rate.
     */
    private var lastAltitude: Double? = null
    /**
     * Timestamp of the last altitude update, used to calculate ascent rate.
     */
    private var lastTime: Long? = null

    /**
     * Previously reported risk level.
     *
     * Used to detect changes and prevent duplicate notifications.
     */
    private var lastRiskLevel: RiskLevel = RiskLevel.LOW
    /**
     * Coroutine job responsible for repeatedly notifying the user
     * when high-risk conditions persist.
     */
    private var highRiskLoopJob: Job? = null

    // --- 1. DEFINE SYMPTOMS & WEIGHTS ---
    /**
     * List of supported altitude sickness symptoms and their risk weights.
     *
     * This acts as the single source of truth for mapping UI symptom names
     * to weighted [Symptom] objects used by the risk calculator.
     */
    private val availableSymptoms = listOf(
        Symptom("Headache", 1.0),
        Symptom("Fatigue", 1.0),
        Symptom("Dizziness", 2.0),
        Symptom("Nausea", 2.0),
        Symptom("Vomiting", 5.0),
        Symptom("Confusion", 4.0)
    )

    // --- 2. UPDATED FUNCTION ---
    /**
     * Updates the altitude sickness risk based on user-selected symptoms.
     *
     * Converts symptom names received from the UI into weighted [Symptom]
     * objects, combines them with the current ascent rate, and recalculates
     * the overall [RiskLevel].
     *
     * Triggers notifications if the risk level changes.
     *
     * @param checkedSymptomNames List of symptom names currently selected
     * by the user.
     */
    fun updateRisk(checkedSymptomNames: List<String>) {
        val currentAscentRate = _state.value.ascentRate

        // Map the checked names to our Symptom objects
        val symptomObjects = availableSymptoms.map { symptom ->
            // Create a copy of the symptom with 'checked' set to true if the name is in the list
            symptom.copy(checked = checkedSymptomNames.contains(symptom.name))
        }

        // Pass the objects to the calculator
        val newRisk = AltitudeRiskCalculator.calculateRisk(currentAscentRate, symptomObjects)

        if (newRisk != lastRiskLevel) {
            handleRiskChange(newRisk)
        }

        lastRiskLevel = newRisk
        _state.update { it.copy(riskLevel = newRisk) }
    }

    /**
     * Handles transitions between risk levels.
     *
     * Cancels any ongoing high-risk notification loops and triggers
     * appropriate user notifications based on the new risk level.
     *
     * @param newRisk Newly calculated risk level.
     */
    private fun handleRiskChange(newRisk: RiskLevel) {
        highRiskLoopJob?.cancel()

        when (newRisk) {
            RiskLevel.LOW -> {
                // No notification
            }
            RiskLevel.MODERATE -> {
                notificationHelper.showRiskNotification(
                    "Moderate Risk Warning",
                    "Your ascent rate or symptoms indicate moderate risk. Slow down."
                )
            }
            RiskLevel.HIGH -> {
                startHighRiskLoop()
            }
        }
    }

    /**
     * Starts a repeating notification loop for high-risk conditions.
     *
     * Notifies the user at regular intervals until the risk level
     * drops below [RiskLevel.HIGH].
     */
    private fun startHighRiskLoop() {
        highRiskLoopJob = viewModelScope.launch {
            while (true) {
                notificationHelper.showRiskNotification(
                    "DANGER: High Altitude Risk",
                    "Descend immediately! Critical symptoms or ascent rate detected."
                )
                delay(10 * 60 * 1000) // 10 minutes
            }
        }
    }

    /**
     * Starts continuous altitude tracking using high-accuracy GPS updates.
     *
     * Requests location updates only once and ignores repeated calls
     * while tracking is already active.
     *
     * Callers must ensure location permissions have been granted.
     */
    @SuppressLint("MissingPermission")
    fun startTracking() {
        if (locationCallback != null) return

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
            .setMinUpdateDistanceMeters(5f)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                for (location in result.locations) {
                    processLocation(location)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback!!,
            Looper.getMainLooper()
        )
    }

    /**
     * Processes a new location update and updates altitude state.
     *
     * Calculates ascent or descent rate based on the previous altitude
     * and timestamp, then updates the observable [AltitudeState].
     *
     * @param location Latest location update containing altitude data.
     */
    private fun processLocation(location: Location) {
        val currentAlt = location.altitude
        val currentTime = System.currentTimeMillis()

        var rate = 0.0
        if (lastAltitude != null && lastTime != null) {
            val altDiff = currentAlt - lastAltitude!!
            val timeDiffMin = (currentTime - lastTime!!) / 60000.0

            if (timeDiffMin > 0.001) {
                rate = altDiff / timeDiffMin
            }
        }

        lastAltitude = currentAlt
        lastTime = currentTime

        _state.update {
            it.copy(
                currentAltitude = currentAlt,
                ascentRate = rate
            )
        }
    }
}