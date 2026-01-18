package com.example.myapplication.logger

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class LocationRepository(context: Context) {

    private val client = LocationServices.getFusedLocationProviderClient(context)

    private val request = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        10_000L // every 10 seconds
    ).setMinUpdateDistanceMeters(5f).build()

    @SuppressLint("MissingPermission")
    fun locationUpdates() = callbackFlow<Location> {
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { trySend(it) }
            }
        }

        client.requestLocationUpdates(request, callback, null)
        awaitClose { client.removeLocationUpdates(callback) }
    }
}