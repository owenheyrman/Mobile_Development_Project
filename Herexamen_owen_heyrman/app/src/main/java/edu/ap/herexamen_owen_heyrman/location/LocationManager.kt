package edu.ap.herexamen_owen_heyrman.location

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class LocationManager(context: Context) : ViewModel() {

    private val locationHelper = LocationHelper(context)
    private val geocodingHelper = GeocodingHelper()

    fun fetchCurrentLocationAndAddress(onLocationReceived: (Triple<Double?, Double?, String?>) -> Unit) {
        Log.d(TAG, "Fetching current location and address")

        locationHelper.getLastLocation { location: Location? ->
            location?.let {
                val latitude = it.latitude
                val longitude = it.longitude
                Log.d(TAG, "Location received: latitude=$latitude, longitude=$longitude")

                viewModelScope.launch {
                    val address = geocodingHelper.getLocationAddress(latitude, longitude)
                    Log.d(TAG, "Address fetched: $address")
                    onLocationReceived(Triple(latitude, longitude, address))
                }
            } ?: run {
                Log.d(TAG, "No location available")
                onLocationReceived(Triple(null, null, null))
            }
        }
    }

    companion object {
        private const val TAG = "LocationManager"
    }
}