package com.app.permission.internal

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.app.permission.AppPermission
import com.app.permission.PermissionHandler
import com.app.permission.PermissionResult
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

internal class DefaultPermissionHandler(
    private val context: Context
) : PermissionHandler, PermissionObserver {
    private val sharedFlow: MutableSharedFlow<PermissionResult> by lazy {
        MutableSharedFlow(
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
            replay = 1
        )
    }
    override val flow: Flow<PermissionResult>
        get() = sharedFlow

    override fun checkAndAskPermissionGroup(requestCode: Int, appPermissions: List<AppPermission>) {
        var hasPermission = true

        for (permission in appPermissions) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    permission.name
                ) == PackageManager.PERMISSION_DENIED
            ) {
                hasPermission = false
                break
            }
        }

        if (hasPermission) {
            val permissionMap = HashMap<AppPermission, Boolean>()

            for (appPermission in appPermissions) {
                permissionMap[appPermission] = true
            }

            sharedFlow.tryEmit(
                PermissionResult(
                    requestCode,
                    permissionMap
                )
            )
        } else {
            val intent = Intent(context, PermissionActivity::class.java)

            intent.putExtra(PermissionActivity.KEY_PERMISSION_REQUEST_CODE, requestCode)
            intent.putExtra(
                PermissionActivity.KEY_PERMISSIONS,
                appPermissions.map { it.permission }.toTypedArray()
            )

            context.startActivity(
                intent
            )
        }
    }

    override fun updatePermissionResult(permissionResult: PermissionResult) {
        sharedFlow.tryEmit(permissionResult)
    }
}