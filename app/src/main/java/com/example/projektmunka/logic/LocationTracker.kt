package com.example.projektmunka.logic

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.example.projektmunka.utils.Constants
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class LocationTracker @Inject constructor(private val context: Context) {

    private val job = SupervisorJob()
    val scope = CoroutineScope(Dispatchers.IO + job)

    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation = _currentLocation.asStateFlow()

    private val _locationDataFlow = MutableStateFlow<Pair<Location, Long>?>(null)
    val locationDataFlow = _locationDataFlow.asStateFlow()

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    val locationRequest = LocationRequest.create().apply {
        interval = Constants.LOCATION_UPDATE_INTERVAL
        fastestInterval = Constants.FASTEST_LOCATION_UPDATE_INTERVAL
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult?.lastLocation?.let { location ->
                val timestamp = System.currentTimeMillis()
                val newLocationData = Pair(location, timestamp)
                _locationDataFlow.value = newLocationData
                _currentLocation.value = location
            }
        }
    }

    init {
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            try {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    null
                )
            } catch (e: SecurityException) {
                // Kezelés a SecurityException esetén (pl. engedély megtagadva)
                e.printStackTrace()
            }
        } else {
            // Kezelés, ha nincs meg az engedély
            // Például: kérjen engedélyt a felhasználótól
        }
    }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}