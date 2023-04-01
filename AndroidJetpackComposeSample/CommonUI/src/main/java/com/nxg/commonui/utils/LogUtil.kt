package com.nxg.commonui.utils

import android.util.Log

object LogUtil {
    fun printLog(tag: String = "ComposePlane", message: String) {
        Log.d(tag, message)
    }
}