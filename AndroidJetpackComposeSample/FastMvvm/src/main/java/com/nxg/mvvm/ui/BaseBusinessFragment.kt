package com.nxg.mvvm.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import com.nxg.mvvm.logger.SimpleLogger

var PERMISSIONS_REQUIRED = arrayOf(
    Manifest.permission.CAMERA,
    Manifest.permission.RECORD_AUDIO,
    Manifest.permission.WRITE_EXTERNAL_STORAGE
)

open class BaseBusinessFragment : BaseViewModelFragment, SimpleLogger {

    constructor() : super()

    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    /**
     * 改成获取权限导航到指定Fragment
     */
    val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            logger.debug { "activityResultLauncher:" }
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in PERMISSIONS_REQUIRED && !it.value)
                    permissionGranted = false
            }
            logger.debug { "activityResultLauncher: permissionGranted $permissionGranted" }
            if (permissionGranted) {
                doWhenPermissionGranted()
            } else {
                doWhenPermissionNotGranted()
            }
        }

    open fun doWhenPermissionNotGranted() {
        Toast.makeText(context, "Permission request denied", Toast.LENGTH_LONG).show()
    }

    /**
     * 获取权限后需要执行的逻辑
     */
    open fun doWhenPermissionGranted() {
        logger.debug { "doWhenPermissionGranted" }
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
     * 请求获取单个权限
     */
    fun requestPermission(@NonNull permission: String) {
        requestPermission(arrayOf(permission))
    }

    /**
     * 请求获取多个权限
     */
    fun requestPermission(permissions: Array<String>) {
        activityResultLauncher.launch(permissions)
    }
}