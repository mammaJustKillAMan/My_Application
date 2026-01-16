package com.example.myapplication.altitude

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await

class AltitudeRepository(context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission") // Permissions handled elsewhere
    suspend fun getCurrentAltitude(): Double? {
        return try {
            val location = fusedLocationClient.lastLocation.await()
            location?.altitude
        } catch (e: Exception) {
            null
        }
    }
}
