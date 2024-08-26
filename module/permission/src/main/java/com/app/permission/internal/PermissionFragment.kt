package com.app.permission.internal

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.app.permission.AppPermission
import com.app.permission.PermissionFactory
import com.app.permission.PermissionResult

internal class PermissionFragment : Fragment() {
    // todo Hilt
    private val observer: PermissionObserver by lazy {
        PermissionFactory.getPermissionObserver(this.requireContext())
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { map ->
        arguments?.getInt(KEY_PERMISSION_REQUEST_CODE)?.let {
            observer.updatePermissionResult(
                PermissionResult(
                    it,
                    map.mapKeys { (key, _) -> AppPermission.valueOf(key) }
                )
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.getStringArray(KEY_PERMISSIONS).let {
            permissionLauncher.launch(
                it
            )
        }
    }

    companion object {
        private const val KEY_PERMISSIONS = "KEY_PERMISSIONS"
        private const val KEY_PERMISSION_REQUEST_CODE = "KEY_PERMISSION_REQUEST_CODE"

        internal fun newInstance(appPermissions: List<AppPermission>): PermissionFragment {
            val fragment = PermissionFragment()

            val arguments = Bundle()
            arguments.putStringArray(
                KEY_PERMISSIONS,
                appPermissions.map { it.name }.toTypedArray()
            )
            fragment.arguments = arguments

            return fragment
        }
    }
}
