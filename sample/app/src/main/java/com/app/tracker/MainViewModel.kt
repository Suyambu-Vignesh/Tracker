package com.app.tracker

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private var isDone = false

    internal fun trackLocation(context: Context) {
        if (isDone) {
            return
        }

        isDone = true
        viewModelScope.launch {
            LocationTrackerFactory.getLocationTrackerFactory(context)
                .checkPermissionAndTrackLocation().collectLatest {
                    if (it is PermissionError) {
                        Log.i("StateOfLoc", "Error Permission")
                    } else if (it is LocationInfo) {
                        Log.i("StateOfLoc", "lat = ${it.latitude}, long = ${it.location}")
                    }
                }
        }
    }
}