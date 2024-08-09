package com.twiceyuan.wxapk

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import com.twiceyuan.wxapk.databinding.ActivityMainBinding

open class MainActivity : Activity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupVisibleInLauncher()
    }

    private fun setupVisibleInLauncher() {
        val componentName = ComponentName(this, MainActivity::class.java)
        val setting = packageManager.getComponentEnabledSetting(componentName)

        val isHidden = setting == PackageManager.COMPONENT_ENABLED_STATE_DISABLED

        fun toggleComponentEnable() = when (isHidden) {
            true -> packageManager.setComponentEnabledSetting(
                componentName,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )

            false -> packageManager.setComponentEnabledSetting(
                componentName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )
        }

        val onClickListener: (View) -> Unit = {
            toggleComponentEnable()
            toast(R.string.set_success)
            this.finish()
        }

        // 展示之前的状态，是否已被隐藏
        binding.apply {
            switchHideIcon.isChecked = isHidden
            switchHideIcon.setOnClickListener(onClickListener)
            layoutHideIcon.setOnClickListener(onClickListener)
        }

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
