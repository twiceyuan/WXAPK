package com.twiceyuan.wxapk

import android.app.Activity
import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*


open class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupVisibleInLauncher()
    }

    private fun setupVisibleInLauncher() {
        val componentName = ComponentName(this, MainActivity::class.java)
        val setting = packageManager.getComponentEnabledSetting(componentName)

        val isHidden = setting == PackageManager.COMPONENT_ENABLED_STATE_DISABLED

        fun toggleComponentEnable() = when(isHidden) {
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

        val onClickListener: (View) -> Unit =  {
            toggleComponentEnable()
            AppInstance.get(this).toast(R.string.set_success)
            this.finish()
        }

        // 展示之前的状态，是否已被隐藏
        switch_hide_icon.isChecked = isHidden
        switch_hide_icon.setOnClickListener(onClickListener)
        layout_hide_icon.setOnClickListener(onClickListener)
    }
}
