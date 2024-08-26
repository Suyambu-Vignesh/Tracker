package com.app.tracker

sealed interface LocationResult

/**
 * Location information
 */
data class LocationInfo(
    val location: Double,
    val latitude: Double,
    val altitude: Double
) : LocationResult

/**
 * Permission Related Error. User has not given consent
 */
data object PermissionError : LocationResult


