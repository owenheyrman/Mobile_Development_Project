package edu.ap.herexamen_owen_heyrman.location

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class GeocodingHelper {

    private val client = OkHttpClient()

    suspend fun getLocationAddress(latitude: Double, longitude: Double): String? {
        val url = "https://nominatim.openstreetmap.org/reverse?format=json&lat=$latitude&lon=$longitude"
        Log.d(TAG, "Fetching address for coordinates: latitude=$latitude, longitude=$longitude")

        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                val json = response.body?.string()

                json?.let {
                    val jsonObject = JSONObject(it)
                    val address = jsonObject.getString("display_name")
                    Log.d(TAG, "Address fetched: $address")
                    return@withContext address
                } ?: run {
                    Log.d(TAG, "No address found in response")
                    return@withContext null
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching address", e)
                return@withContext null
            }
        }
    }

    companion object {
        private const val TAG = "GeocodingHelper"
    }
}