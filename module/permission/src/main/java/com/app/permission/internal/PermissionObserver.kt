package com.app.permission.internal

import com.app.permission.PermissionResult

interface PermissionObserver {

    fun updatePermissionResult(permissionResult: PermissionResult)
}