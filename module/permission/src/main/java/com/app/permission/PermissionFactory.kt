package com.app.permission

import android.content.Context
import com.app.permission.internal.DefaultPermissionHandler
import com.app.permission.internal.PermissionObserver

class PermissionFactory {
    companion object {
        // todo this will create mem leak, replace with hilt
        private var handler: DefaultPermissionHandler? = null


        fun getPermissionHandler(context: Context): PermissionHandler {
            return getInstance(context)
        }

        fun getPermissionObserver(context: Context): PermissionObserver {
            return getInstance(context)
        }

        private fun getInstance(context: Context): DefaultPermissionHandler {
            return handler?.let {
                it
            } ?: run {
                val defaultPermissionHandler =
                    DefaultPermissionHandler(context)
                handler = defaultPermissionHandler
                defaultPermissionHandler
            }
        }
    }


}
