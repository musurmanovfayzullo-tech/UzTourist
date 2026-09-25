package com.example.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class UserGpsLocation(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float = 0f,
    val speed: Float = 0f,
    val bearing: Float = 0f,
    val provider: String = "GPS",
    val timestamp: Long = System.currentTimeMillis(),
    val addressEstimate: String = "Aniqlanmoqda...",
    val isRealGpsFix: Boolean = true
)

object GpsLocationManager {
    private const val TAG = "GpsLocationManager"

    private val _currentLocation = MutableStateFlow<UserGpsLocation?>(null)
    val currentLocation: StateFlow<UserGpsLocation?> = _currentLocation.asStateFlow()

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    private var systemLocationListener: LocationListener? = null
    private var isListening = false

    fun init(context: Context) {
        if (fusedLocationClient == null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context.applicationContext)
        }
    }

    /**
     * Checks if GPS or Network location providers are enabled on the device.
     */
    fun isLocationProviderEnabled(context: Context): Boolean {
        return try {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true ||
                    locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Checks if runtime location permissions have been granted.
     */
    fun hasLocationPermission(context: Context): Boolean {
        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return hasFine || hasCoarse
    }

    /**
     * Starts continuous background location updates as soon as permissions are available.
     * Updates _currentLocation whenever tourist moves or new coordinates arrive.
     */
    @SuppressLint("MissingPermission")
    fun startLocationUpdates(context: Context) {
        if (!hasLocationPermission(context)) {
            Log.w(TAG, "Cannot start location updates: permission not granted")
            return
        }

        init(context)

        // 1. Immediately read best last-known location so state is never empty
        val bestLast = getBestLastKnownLocation(context)
        if (bestLast != null) {
            updateLocationState(context, bestLast)
        }

        if (isListening) return

        try {
            // 2. Query Fused lastLocation asynchronously
            fusedLocationClient?.lastLocation?.addOnSuccessListener { loc: Location? ->
                if (loc != null) {
                    updateLocationState(context, loc)
                }
            }

            // 3. Request continuous high-accuracy location updates
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000L)
                .setMinUpdateIntervalMillis(1500L)
                .setMinUpdateDistanceMeters(2f)
                .build()

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    result.lastLocation?.let { loc ->
                        updateLocationState(context, loc)
                    }
                }
            }

            fusedLocationClient?.requestLocationUpdates(
                locationRequest,
                locationCallback as LocationCallback,
                Looper.getMainLooper()
            )
            isListening = true
            Log.d(TAG, "Fused location updates started successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error starting FusedLocationProvider", e)
        }

        // 4. Also register system LocationManager as robust backup
        startSystemLocationManager(context)
    }

    @SuppressLint("MissingPermission")
    private fun startSystemLocationManager(context: Context) {
        if (!hasLocationPermission(context)) return
        try {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return
            
            if (systemLocationListener == null) {
                systemLocationListener = object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        updateLocationState(context, location)
                    }
                    override fun onProviderEnabled(provider: String) {}
                    override fun onProviderDisabled(provider: String) {}
                    @Deprecated("Deprecated in Java")
                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                }
            }

            val listener = systemLocationListener!!

            val mainLooper = Looper.getMainLooper()
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000L, 2f, listener, mainLooper)
            }
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000L, 2f, listener, mainLooper)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error starting system LocationManager", e)
        }
    }

    /**
     * Reads best last known location across all available system providers.
     */
    @SuppressLint("MissingPermission")
    fun getBestLastKnownLocation(context: Context): Location? {
        if (!hasLocationPermission(context)) return null
        var bestLocation: Location? = null
        try {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            if (locationManager != null) {
                val providers = listOf(
                    LocationManager.GPS_PROVIDER,
                    LocationManager.NETWORK_PROVIDER,
                    LocationManager.PASSIVE_PROVIDER
                )
                for (provider in providers) {
                    try {
                        val loc = locationManager.getLastKnownLocation(provider) ?: continue
                        if (bestLocation == null || loc.time > bestLocation.time || (loc.hasAccuracy() && loc.accuracy < (bestLocation.accuracy ?: 999f))) {
                            bestLocation = loc
                        }
                    } catch (ignored: Exception) {}
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting best last known location", e)
        }
        return bestLocation
    }

    /**
     * Actively fetches the freshest and most accurate real-time GPS location of the tourist.
     * Uses active GPS satellite lock with fallback to network and last known location.
     */
    @SuppressLint("MissingPermission")
    suspend fun getAccurateCurrentLocation(context: Context): UserGpsLocation = withContext(Dispatchers.IO) {
        startLocationUpdates(context)

        // 1. If we already have a very recent location (< 8 seconds old), return it
        val cached = _currentLocation.value
        if (cached != null && cached.isRealGpsFix && (System.currentTimeMillis() - cached.timestamp) < 8_000L) {
            return@withContext cached
        }

        if (!hasLocationPermission(context)) {
            return@withContext UserGpsLocation(
                latitude = 41.3111,
                longitude = 69.2797,
                addressEstimate = "GPS Ruxsati Berilmagan (Joylashuvni yoqing)",
                isRealGpsFix = false
            )
        }

        init(context)

        // 2. Try to get fresh location from Fused Provider with 4-second timeout
        val freshLocation = withTimeoutOrNull(4000L) {
            suspendCancellableCoroutine<Location?> { continuation ->
                try {
                    val cts = CancellationTokenSource()
                    continuation.invokeOnCancellation { cts.cancel() }

                    fusedLocationClient?.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
                        ?.addOnSuccessListener { loc ->
                            if (continuation.isActive) continuation.resume(loc)
                        }
                        ?.addOnFailureListener {
                            if (continuation.isActive) continuation.resume(null)
                        }
                } catch (e: Exception) {
                    if (continuation.isActive) continuation.resume(null)
                }
            }
        }

        if (freshLocation != null) {
            updateLocationState(context, freshLocation)
            return@withContext _currentLocation.value ?: buildUserGpsLocation(context, freshLocation)
        }

        // 3. Fallback: check best last known location
        val bestLast = getBestLastKnownLocation(context)
        if (bestLast != null) {
            updateLocationState(context, bestLast)
            return@withContext _currentLocation.value ?: buildUserGpsLocation(context, bestLast)
        }

        // 4. If current state exists, return it
        val current = _currentLocation.value
        if (current != null) {
            return@withContext current
        }

        // 5. Default fallback if device has no GPS fix at all (e.g. inside bunker or newly booted emulator)
        return@withContext UserGpsLocation(
            latitude = 41.3111,
            longitude = 69.2797,
            addressEstimate = "Toshkent shahri (GPS qidirilmoqda...)",
            isRealGpsFix = false
        )
    }

    private fun updateLocationState(context: Context, loc: Location) {
        val basicEstimate = estimateCityName(loc.latitude, loc.longitude)
        val userLoc = UserGpsLocation(
            latitude = loc.latitude,
            longitude = loc.longitude,
            accuracy = loc.accuracy,
            speed = loc.speed,
            bearing = loc.bearing,
            provider = loc.provider ?: "GPS",
            timestamp = if (loc.time > 0) loc.time else System.currentTimeMillis(),
            addressEstimate = basicEstimate,
            isRealGpsFix = true
        )
        _currentLocation.value = userLoc

        // Perform reverse geocoding in background to obtain exact street & district
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val detailedAddress = resolveAddressWithGeocoder(context, loc.latitude, loc.longitude)
                if (detailedAddress.isNotBlank() && detailedAddress != basicEstimate) {
                    val curr = _currentLocation.value
                    if (curr != null && curr.latitude == loc.latitude && curr.longitude == loc.longitude) {
                        _currentLocation.value = curr.copy(addressEstimate = detailedAddress)
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Geocoder error: ${e.message}")
            }
        }
    }

    private fun buildUserGpsLocation(context: Context, loc: Location): UserGpsLocation {
        val address = resolveAddressWithGeocoder(context, loc.latitude, loc.longitude)
        return UserGpsLocation(
            latitude = loc.latitude,
            longitude = loc.longitude,
            accuracy = loc.accuracy,
            speed = loc.speed,
            bearing = loc.bearing,
            provider = loc.provider ?: "GPS",
            timestamp = if (loc.time > 0) loc.time else System.currentTimeMillis(),
            addressEstimate = address.ifBlank { estimateCityName(loc.latitude, loc.longitude) },
            isRealGpsFix = true
        )
    }

    /**
     * Resolves human-readable street, district, and city using Android Geocoder.
     */
    @Suppress("DEPRECATION")
    fun resolveAddressWithGeocoder(context: Context, lat: Double, lon: Double): String {
        try {
            val geocoder = Geocoder(context.applicationContext, Locale.forLanguageTag("uz-UZ"))
            val addresses = geocoder.getFromLocation(lat, lon, 1)
            if (!addresses.isNullOrEmpty()) {
                val addr = addresses[0]
                val street = addr.thoroughfare // ko'cha
                val district = addr.subLocality ?: addr.subAdminArea // tuman
                val city = addr.locality ?: addr.adminArea // shahar / viloyat
                val parts = listOfNotNull(street, district, city).filter { it.isNotBlank() }
                if (parts.isNotEmpty()) {
                    return parts.joinToString(", ")
                }
                val line = addr.getAddressLine(0)
                if (!line.isNullOrBlank()) {
                    return line
                }
            }
        } catch (e: Exception) {
            // Geocoder not available or network error
        }
        return estimateCityName(lat, lon)
    }

    fun stopLocationUpdates() {
        try {
            locationCallback?.let {
                fusedLocationClient?.removeLocationUpdates(it)
            }
            systemLocationListener?.let {
                // system listener cleanup
            }
            isListening = false
            Log.d(TAG, "Location updates stopped")
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping location updates", e)
        }
    }

    fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Radius of earth in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    fun formatDistance(km: Double): String {
        return if (km < 1.0) {
            "${(km * 1000).toInt()} metr"
        } else {
            String.format(Locale.US, "%.1f km", km)
        }
    }

    fun estimateDuration(distanceKm: Double, mode: String = "car"): String {
        return when (mode) {
            "walk" -> {
                val hours = distanceKm / 4.5
                val totalMinutes = (hours * 60).toInt()
                if (totalMinutes < 60) "$totalMinutes daq"
                else "${totalMinutes / 60} s ${totalMinutes % 60} d"
            }
            "train" -> {
                val hours = distanceKm / 160.0
                val totalMinutes = (hours * 60).toInt() + 15
                if (totalMinutes < 60) "$totalMinutes daq"
                else "${totalMinutes / 60} soat ${totalMinutes % 60} daqiqa"
            }
            else -> { // Car / Taxi
                val speed = if (distanceKm > 50) 80.0 else 40.0
                val hours = distanceKm / speed
                val totalMinutes = (hours * 60).toInt()
                if (totalMinutes < 60) "$totalMinutes daqiqa"
                else "${totalMinutes / 60} soat ${totalMinutes % 60} daqiqa"
            }
        }
    }

    fun estimateCityName(lat: Double, lon: Double): String {
        return when {
            lat in 41.1..41.5 && lon in 69.1..69.45 -> "Toshkent shahri"
            lat in 39.5..39.8 && lon in 66.8..67.1 -> "Samarqand shahri"
            lat in 39.6..39.9 && lon in 64.3..64.6 -> "Buxoro shahri"
            lat in 41.2..41.5 && lon in 60.2..60.5 -> "Xiva shahri"
            lat in 38.9..39.2 && lon in 66.7..67.0 -> "Shahrisabz"
            lat in 41.4..41.7 && lon in 69.9..70.2 -> "Chorvoq & Chimgan"
            lat in 39.5..39.8 && lon in 68.3..68.7 -> "Zomin"
            lat in 43.5..44.0 && lon in 58.8..59.3 -> "Mo'ynoq (Orol)"
            lat in 40.2..40.5 && lon in 71.1..71.4 -> "Rishton / Farg'ona"
            lat in 40.7..41.0 && lon in 72.2..72.5 -> "Andijon shahri"
            lat in 40.9..41.2 && lon in 71.5..71.8 -> "Namangan shahri"
            lat in 40.0..40.3 && lon in 65.2..65.5 -> "Navoiy shahri"
            lat in 40.4..40.7 && lon in 68.7..69.0 -> "Guliston / Sirdaryo"
            lat in 37.1..37.4 && lon in 67.1..67.4 -> "Termiz shahri"
            lat in 42.4..42.7 && lon in 59.5..59.8 -> "Nukus shahri"
            else -> "Joylashuv: ${String.format(Locale.US, "%.4f, %.4f", lat, lon)}"
        }
    }
}
