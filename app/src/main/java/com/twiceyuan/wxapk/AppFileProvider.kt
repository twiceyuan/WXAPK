package com.twiceyuan.wxapk

class AppFileProvider : androidx.core.content.FileProvider() {
    companion object {
        const val AUTHORITY_SUFFIX = ".AppFileProvider"
    }
}