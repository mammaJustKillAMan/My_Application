package com.example.myapplication.altitude

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Repository responsible for providing continuous location updates.
 *
 * Uses the Fused Location Provider to emit high-accuracy [Location] updates
 * as a cold [Flow]. Location updates automatically start when the flow
 * is collected and stop when the collection is cancelled.
 *
 * @param context Application context used to initialize the location client.
 */
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
