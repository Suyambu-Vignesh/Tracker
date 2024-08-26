package com.app.tracker.internal

import com.google.android.gms.location.Priority

data class LocationConfig(
    val priority: Int = Priority.PRIORITY_HIGH_ACCURACY,
    val intervalMillis: Long = 10000L,
    val minIntervalMills: Long = 5000L
)
