package com.example.myapplication.logger

import android.location.Location
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class RouteRecorderViewModel : ViewModel() {

    private val _routePoints = mutableStateListOf<RoutePoint>()
    val routePoints: List<RoutePoint> = _routePoints

    fun addLocation(location: Location) {
        _routePoints.add(
            RoutePoint(
                latitude = location.latitude,
                longitude = location.longitude,
                altitude = location.altitude,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    fun clearRoute() {
        _routePoints.clear()
    }

    fun isEmpty(): Boolean = _routePoints.isEmpty()
}