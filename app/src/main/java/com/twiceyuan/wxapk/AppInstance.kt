package com.twiceyuan.wxapk

import android.app.Application
import android.content.Context
import android.widget.Toast

class AppInstance : Application() {

    companion object {
        fun get(context: Context) = context.applicationContext as AppInstance
    }

    fun toast(resId: Int) {
        Toast.makeText(applicationContext, resId, Toast.LENGTH_SHORT).show()
    }
}