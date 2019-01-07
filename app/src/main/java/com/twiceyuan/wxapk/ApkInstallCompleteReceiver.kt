package com.twiceyuan.wxapk

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * APK 安装完成之后清除临时文件夹
 */
class ApkInstallCompleteReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return
        intent ?: return

        context.applicationContext.unregisterReceiver(this)

        Log.i("ApkInstall", intent.action)
        val tempDir = context.getExternalFilesDir(Constants.TEMP_APK_PATH) ?: return
        tempDir.listFiles().forEach { it.delete() }
    }
}