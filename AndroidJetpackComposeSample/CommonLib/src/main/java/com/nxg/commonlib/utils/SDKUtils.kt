package com.nxg.commonlib.utils

import android.app.Application

class SDKUtils private constructor(
    private val application: Application
) {

    companion object {
        private var instance: SDKUtils? = null

        fun init(application: Application) {
            instance ?: synchronized(this) {
                instance ?: SDKUtils(application).also { instance = it }
            }
        }

        fun getApplicationContext(): Application {
            return instance?.application ?: throw NullPointerException("SDKUtils instance == null")
        }
    }

}