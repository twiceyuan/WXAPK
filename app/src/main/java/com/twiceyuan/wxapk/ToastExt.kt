package com.twiceyuan.wxapk

import android.content.Context
import android.widget.Toast

fun Context.toast(resId: Int) {
    Toast.makeText(applicationContext, resId, Toast.LENGTH_SHORT).show()
}
