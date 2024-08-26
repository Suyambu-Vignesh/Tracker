package com.app.tracker.internal

import android.content.Context
import com.app.permission.AppPermission
import com.app.permission.PermissionFactory
import com.app.permission.PermissionHandler
import com.app.tracker.LocationResult
import com.app.tracker.LocationTracker
import com.app.tracker.PermissionError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class DefaultLocationTracker(
    private val context: Context,
    private val permissionHandler: PermissionHandler = PermissionFactory.getPermissionHandler(context)
) : LocationTracker, CoroutineScope {
    private val job = SupervisorJob()
    private val locationManager: GmsFusedLocationManager by lazy {
        GmsFusedLocationManager(context)
    }
    private val sharedFlow: MutableSharedFlow<LocationResult> by lazy {
        MutableSharedFlow(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    }

    override val coroutineContext: CoroutineContext = Dispatchers.Main + job

    override fun checkPermissionAndTrackLocation(): Flow<LocationResult> {
        sharedFlow.subscriptionCount.map { it == 0 }.distinctUntilChanged().onEach {
            if (it) {
                stopTracking()
            }
        }.launchIn(scope = this)

        if (sharedFlow.subscriptionCount.value > 0) {
            //someone already listening we can return the flow

            return sharedFlow
        }

        launch {
            withContext(Dispatchers.Main) {
                permissionHandler.flow.collect { permissionResult ->
                    if (permissionResult.requestCode == REQUEST_CODE) {
                        if (permissionResult.result.containsValue(true)) {
                            trackLocation()
                        } else {
                            sharedFlow.emit(PermissionError)
                        }
                    }
                }
            }
        }

        permissionHandler.checkAndAskPermissionGroup(
            REQUEST_CODE,
            listOf(
                AppPermission.PERMISSION_COARSE_LOCATION,
                AppPermission.PERMISSION_FINE_LOCATION
            )
        )
        return sharedFlow
    }

    private suspend fun trackLocation() {
        launch {
            locationManager.locationFlow.collectLatest {
                sharedFlow.emit(it)
            }
        }

        launch {
            locationManager.observeLocation(
                LocationConfig()
            )
        }
    }

    private fun stopTracking() {
        job.cancel()
    }

    companion object {
        private const val REQUEST_CODE = -101
    }
}
