package com.twiceyuan.wxapk

import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import androidx.core.content.FileProvider
import com.twiceyuan.wxapk.Constants.TEMP_APK_PATH
import java.io.File
import java.io.FileOutputStream


/**
 * Created by twiceYuan on 2018/3/5.
 *
 * 安装意图分发 apk.1 -> apk installer
 */
class InstallerActivity : PermissionHandlerActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerInstallReceiver()

        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        intent?.data?.let { install(it) }
    }

    // 注册安装结束的事件监听，用于清除缓存文件
    private fun registerInstallReceiver() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED)
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED)
        intentFilter.addDataScheme("package")
        applicationContext.registerReceiver(ApkInstallCompleteReceiver(), intentFilter)
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
        fun installAction() {
            val newUri = paramUri.convertToInsideUri() ?: return
            val installerIntent = Intent(Intent.ACTION_VIEW)
            installerIntent.setDataAndType(newUri, Constants.INTENT_TYPE_INSTALL)
            installerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            installerIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(installerIntent)
            finish()
        }

        // 微信 7.0 以下使用的是 file uri，需要申请文件读取权限才能读取创建临时文件
        when (paramUri.scheme) {
            "file" -> requestStorageReadPermission { installAction() }
            "content" -> installAction()
        }
    }

    // 转换为内部文件的 Uri，方便进行改名
    private fun Uri.convertToInsideUri(): Uri? {
        val inputStream = contentResolver.openInputStream(this) ?: return null
        val tempDir = getExternalFilesDir(TEMP_APK_PATH) ?: return null
        val tempApkFile = File.createTempFile(lastPathSegment ?: "temp", null, tempDir)
        val outputStream = FileOutputStream(tempApkFile)
        inputStream.copyTo(outputStream)
        inputStream.close()
        val authority = getString(R.string.file_provider_authority)
        return FileProvider.getUriForFile(this@InstallerActivity, authority, tempApkFile)
    }
}