package com.example.myapplication.altitude

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
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
