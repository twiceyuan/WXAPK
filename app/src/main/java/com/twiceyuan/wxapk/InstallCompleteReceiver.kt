package com.twiceyuan.wxapk

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * APK 安装完成之后清除临时文件夹
 */
class InstallCompleteReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val applicationContext = context?.applicationContext ?: return
        applicationContext.unregisterReceiver(this)
        applicationContext.getExternalFilesDir(Constants.TEMP_APK_PATH)
            ?.listFiles()
            ?.forEach { it.delete() }
    }
}