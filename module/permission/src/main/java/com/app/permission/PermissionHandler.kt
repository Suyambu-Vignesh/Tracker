package com.app.permission

import kotlinx.coroutines.flow.Flow

interface PermissionHandler {
    val flow: Flow<PermissionResult>
    fun checkAndAskPermissionGroup(requestCode: Int, appPermissions: List<AppPermission>)

    // todo add for single permission
}