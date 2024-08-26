package com.app.permission

data class PermissionResult(
    val requestCode: Int,
    val result: Map<AppPermission, Boolean>
)