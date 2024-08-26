package com.app.permission

import android.Manifest

enum class AppPermission(val permission: String) {
    PERMISSION_FINE_LOCATION(Manifest.permission.ACCESS_FINE_LOCATION),
    PERMISSION_COARSE_LOCATION(Manifest.permission.ACCESS_COARSE_LOCATION)
}

fun getAppPermission(permission: String): AppPermission {
    return when {
        permission == Manifest.permission.ACCESS_FINE_LOCATION -> {
            AppPermission.PERMISSION_FINE_LOCATION
        }

        else -> {
            AppPermission.PERMISSION_COARSE_LOCATION
        }
    }
}
