package com.app.tracker

import android.content.Context
import com.app.tracker.internal.DefaultLocationTracker

object LocationTrackerFactory {

    fun getLocationTrackerFactory(context: Context): LocationTracker {
        return DefaultLocationTracker(context)
    }
}