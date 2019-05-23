package com.twiceyuan.wxapk

import android.app.Activity
import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
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

        fun showSuccess() {
            toast("设置成功")
            finish()
        }

        // 已经被隐藏状态
        if (setting == PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
            btn_visible_in_launcher.setText(R.string.show_in_launcher)
            btn_visible_in_launcher.setOnClickListener {
                // 重新显示
                packageManager.setComponentEnabledSetting(componentName,
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP)
                showSuccess()
            }
        } else {
            // 图标正常显示状态
            btn_visible_in_launcher.setText(R.string.hide_in_launcher)
            btn_visible_in_launcher.setOnClickListener {
                packageManager.setComponentEnabledSetting(componentName,
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP)
                showSuccess()
            }
        }
    }

    private fun toast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }
}
