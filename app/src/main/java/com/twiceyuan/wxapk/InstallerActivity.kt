package com.twiceyuan.wxapk

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.content.FileProvider
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
        fun installAction(uri: Uri) {
            val installerIntent = Intent(Intent.ACTION_VIEW)
            installerIntent.setDataAndType(uri, "application/vnd.android.package-archive")
            installerIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            installerIntent.putExtra(Intent.EXTRA_RETURN_RESULT, true)
            startActivityForResult(installerIntent, REQUEST_INSTALL_APK)
        }

        when (paramUri.scheme) {
            "file" -> {
                // 微信 7.0 以下使用的是 file uri，需要申请文件读取权限才能读取创建临时文件
                requestStorageReadPermission { installAction(paramUri) }
            }

            "content" -> {
                // 拷贝内沙盒中提供 Uri，避免使用文件权限
                val newUri = paramUri.convertToInsideUri() ?: return
                installAction(newUri)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_INSTALL_APK) {
            clearTempApkFiles()
            finish()
        }
    }

    private fun clearTempApkFiles() {
        getExternalFilesDir(TEMP_APK_PATH)
            ?.listFiles()
            ?.forEach { it.delete() }
    }

    // 转换为内部文件的 Uri，方便进行改名
    private fun Uri.convertToInsideUri(): Uri? {
        val inputStream = contentResolver.openInputStream(this) ?: return null
        val tempDir = getExternalFilesDir(TEMP_APK_PATH) ?: return null
        val tempApkFile =
            File.createTempFile(Uri.encode(lastPathSegment) ?: "temp", ".apk", tempDir)
        val outputStream = FileOutputStream(tempApkFile)
        inputStream.copyTo(outputStream)
        inputStream.close()
        val authority = packageName + AppFileProvider.AUTHORITY_SUFFIX
        return FileProvider.getUriForFile(this@InstallerActivity, authority, tempApkFile)
    }

    companion object {
        const val TEMP_APK_PATH = "temp_apk"
        const val REQUEST_INSTALL_APK = 1001
    }
}
