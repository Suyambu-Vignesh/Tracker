package com.app.tracker

import kotlinx.coroutines.flow.Flow

interface LocationTracker {

    /**
     * Helper fun to tracker the Location and update the result in stream
     */
    fun checkPermissionAndTrackLocation(): Flow<LocationResult>
}
