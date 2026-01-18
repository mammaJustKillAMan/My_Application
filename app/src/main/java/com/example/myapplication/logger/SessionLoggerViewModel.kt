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
        isLogging = false
        locationJob?.cancel()
    }
}