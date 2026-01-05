package com.twiceyuan.wxapk

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.view.WindowCompat
import com.twiceyuan.wxapk.databinding.ActivityMainBinding

open class MainActivity : Activity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.enableEdgeToEdge(window)
        setContentView(binding.root)

        setupVisibleInLauncher()
    }

    private fun setupVisibleInLauncher() {
        binding.layoutFileBrowser.setOnClickListener {
            startActivity(Intent(this, FileBrowserActivity::class.java))
        }

        binding.layoutFileBrowser.setOnLongClickListener {
            applicationContext.contentResolver.persistedUriPermissions.forEach {
                applicationContext.contentResolver.releasePersistableUriPermission(
                    it.uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            startActivity(Intent(this, FileBrowserActivity::class.java))
            true
        }
    }
}
