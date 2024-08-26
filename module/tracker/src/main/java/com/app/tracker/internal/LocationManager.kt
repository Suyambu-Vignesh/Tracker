package com.app.tracker.internal

import com.app.tracker.LocationInfo
import kotlinx.coroutines.flow.Flow

interface LocationManager {
    val locationFlow: Flow<LocationInfo>

    fun observeLocation(config: LocationConfig)
}