package com.twiceyuan.wxapk

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.format.Formatter
import android.view.View
import android.widget.TextView
import androidx.core.view.WindowCompat
import java.io.File

open class MainActivity : Activity() {

    private companion object {
        const val PLAY_DEVELOPER_URL = "https://play.google.com/store/apps/developer?id=twiceYuan"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.enableEdgeToEdge(window)
        setContentView(R.layout.activity_main)

        setupVisibleInLauncher()
    }

    override fun onResume() {
        super.onResume()
        refreshInstallCacheSize()
    }

    private fun setupVisibleInLauncher() {
        findViewById<View>(R.id.layout_file_browser).setOnClickListener {
            startActivity(Intent(this, FileBrowserActivity::class.java))
        }

        findViewById<View>(R.id.layout_file_browser).setOnLongClickListener {
            applicationContext.contentResolver.persistedUriPermissions.forEach {
                applicationContext.contentResolver.releasePersistableUriPermission(
                    it.uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            startActivity(Intent(this, FileBrowserActivity::class.java))
            true
        }

        findViewById<View>(R.id.layout_clear_install_cache).setOnClickListener {
            clearInstallCache()
            refreshInstallCacheSize()
            toast(R.string.install_cache_cleared)
        }

        findViewById<View>(R.id.layout_about_author).setOnClickListener {
            openPlayDeveloperPage()
        }
    }

    private fun refreshInstallCacheSize() {
        val cacheSize = getInstallCacheDir()?.size() ?: 0L
        val formattedSize = Formatter.formatFileSize(this, cacheSize)
        findViewById<TextView>(R.id.text_install_cache_size).text =
            getString(R.string.install_cache_size, formattedSize)
    }

    private fun clearInstallCache() {
        getInstallCacheDir()?.listFiles()?.forEach { it.deleteRecursively() }
    }

    private fun getInstallCacheDir(): File? =
        getExternalFilesDir(InstallerActivity.TEMP_APK_PATH)

    private fun File.size(): Long {
        if (!exists()) return 0L
        if (isFile) return length()
        return listFiles()?.sumOf { it.size() } ?: 0L
    }

    private fun openPlayDeveloperPage() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(PLAY_DEVELOPER_URL))
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            toast(R.string.no_browser_found)
        }
    }
}
