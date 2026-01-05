package com.twiceyuan.wxapk

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build

/**
 * 权限请求的封装
 */
@SuppressLint("Registered")
open class PermissionHandlerActivity : Activity() {

    private var permissionRequestCallback: (() -> Unit)? = null

    fun requestStorageReadPermission(grantedCallback: () -> Unit) {
        permissionRequestCallback = grantedCallback
        requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), grantedCallback.hashCode())
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (permissionRequestCallback?.hashCode() != requestCode) return

        if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            toast(R.string.storage_permission_denied_tip)
            finish()
            return
        }

        permissionRequestCallback?.invoke()
    }
}