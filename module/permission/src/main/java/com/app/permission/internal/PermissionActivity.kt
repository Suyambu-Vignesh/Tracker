package com.app.permission.internal

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.app.permission.AppPermission
import com.app.permission.PermissionFactory
import com.app.permission.PermissionResult
import com.app.permission.getAppPermission

internal class PermissionActivity : AppCompatActivity() {

    private val observer: PermissionObserver by lazy {
        PermissionFactory.getPermissionObserver(this)
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        val code = intent.getIntExtra(KEY_PERMISSION_REQUEST_CODE, -1)

        if (code != -1) {
            observer.updatePermissionResult(
                PermissionResult(
                    code,
                    it.mapKeys { (key, _) -> getAppPermission(key) }
                )
            )
        }

        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        intent.getStringArrayExtra(KEY_PERMISSIONS)?.let {
            permissionLauncher.launch(it)
        }
    }

    companion object {
        internal const val KEY_PERMISSIONS = "KEY_PERMISSIONS"
        internal const val KEY_PERMISSION_REQUEST_CODE = "KEY_PERMISSION_REQUEST_CODE"
    }
}