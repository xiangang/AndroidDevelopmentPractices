package com.nxg.mvvm.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import com.nxg.mvvm.navigation.NavigationCenter
import com.nxg.mvvm.navigation.NavigationDestination

var PERMISSIONS_REQUIRED = arrayOf(
    Manifest.permission.CAMERA,
    Manifest.permission.RECORD_AUDIO,
    Manifest.permission.WRITE_EXTERNAL_STORAGE
)

open class BaseBusinessFragment : BaseViewModelFragment() {

    val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            // Handle Permission granted/rejected
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in PERMISSIONS_REQUIRED && !it.value)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                Toast.makeText(context, "Permission request denied", Toast.LENGTH_LONG).show()
            } else {
                mBaseSharedAndroidViewModel.navigate(NavigationDestination.MAIN)
            }

        }

    /**
     * 检查所有的权限是否已经拥有
     */
    fun checkSelfPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * 检测指定的权限是否已经获取到
     */
    fun checkSelfPermission(@NonNull context: Context, @NonNull permission: String) =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    /**
     * 请求获取权限
     */
    fun requestPermission(@NonNull permission: String) {
        activityResultLauncher.launch(arrayOf(permission))
    }
}