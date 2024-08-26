package com.app.tracker.internal

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.app.tracker.LocationInfo
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

internal class GmsFusedLocationManager(private val context: Context) : LocationManager {
    private val sharedLocationInfoFlow: MutableSharedFlow<LocationInfo> by lazy {
        MutableSharedFlow(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    }

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    private val locationCallback: LocationCallback by lazy {
        object : LocationCallback() {
            override fun onLocationAvailability(locationResult: LocationAvailability) {
                super.onLocationAvailability(locationResult)

            }

            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let {
                    sharedLocationInfoFlow.tryEmit(
                        LocationInfo(
                            it.latitude,
                            it.longitude,
                            it.altitude
                        )
                    )
                }
            }
        }
    }
    override val locationFlow: Flow<LocationInfo>
        get() = sharedLocationInfoFlow

    @SuppressLint("MissingPermission")
    override fun observeLocation(config: LocationConfig) {
        val locationRequest = LocationRequest.Builder(
            config.priority,
            config.intervalMillis
        ).setMinUpdateIntervalMillis(config.minIntervalMills).build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }
}
