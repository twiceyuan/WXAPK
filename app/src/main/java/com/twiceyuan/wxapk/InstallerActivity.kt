package com.twiceyuan.wxapk

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import java.io.File
import java.io.FileOutputStream

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

        val uri = intent?.data
        if (uri != null) {
            install(uri)
        } else {
            finish()
        }
    }

    private fun install(paramUri: Uri) {
        val newUri = convertToInsideUri(paramUri) ?: return
        val installerIntent = Intent(Intent.ACTION_VIEW)
        installerIntent.setDataAndType(newUri, "application/vnd.android.package-archive")
        installerIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(installerIntent)
        finish()
    }

    private fun convertToInsideUri(outsideUri: Uri): Uri? {
        val inputStream = contentResolver.openInputStream(outsideUri) ?: return null
        val externalFilesDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        val downloadPath = File.createTempFile(outsideUri.lastPathSegment, "apk", externalFilesDir)

        val outputStream = FileOutputStream(downloadPath)
        inputStream.copyTo(outputStream)
        inputStream.close()
        return Uri.fromFile(downloadPath)
    }
}