package com.twiceyuan.wxapk

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.WindowCompat

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

        findViewById<View>(R.id.layout_about_author).setOnClickListener {
            openPlayDeveloperPage()
        }
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
