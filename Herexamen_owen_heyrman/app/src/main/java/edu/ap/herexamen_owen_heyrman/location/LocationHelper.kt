package edu.ap.herexamen_owen_heyrman.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task

class LocationHelper(context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun getLastLocation(onLocationReceived: (Location?) -> Unit) {
        Log.d(TAG, "Getting last known location")

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    Log.d(TAG, "Last location received: latitude=${it.latitude}, longitude=${it.longitude}")
                } ?: Log.d(TAG, "Last location is null")
                onLocationReceived(location)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to get last location", e)
                onLocationReceived(null)
            }
    }

    companion object {
        private const val TAG = "LocationHelper"
    }
}