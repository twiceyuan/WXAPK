package com.twiceyuan.wxapk

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast

/**
 * 权限请求的封装
 */
@SuppressLint("Registered")
open class PermissionHandlerActivity : Activity() {

    private var permissionRequestCallback: (() -> Unit)? = null

    fun requestStorageReadPermission(grantedCallback: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionRequestCallback = grantedCallback
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), grantedCallback.hashCode())
        } else {
            grantedCallback()
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (permissionRequestCallback != null && permissionRequestCallback.hashCode() == requestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionRequestCallback?.invoke()
            } else {
                Toast.makeText(
                        applicationContext,
                        "请允许${getString(R.string.app_name)}读取存储权限",
                        Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }
}