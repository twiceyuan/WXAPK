package com.twiceyuan.wxapk

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode

/**
 * Created by twiceYuan on 2018/3/5.
 *
 * 安装意图分发 apk.1 -> apk installer
 */
class InstallerActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        intent?.data?.let { install(it) }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        if (intent?.data != null) {
            install(intent.data)
        } else {
            finish();return
        }
    }

    private fun install(paramUri: Uri) {
        val installerIntent = Intent(Intent.ACTION_VIEW)
        installerIntent.setDataAndType(paramUri, "application/vnd.android.package-archive")
        installerIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(installerIntent)
        finish()
    }
}