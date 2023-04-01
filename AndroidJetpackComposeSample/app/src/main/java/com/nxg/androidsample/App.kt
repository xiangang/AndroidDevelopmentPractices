package com.nxg.androidsample

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.blankj.utilcode.util.Utils
import com.nxg.commonutils.LogUtil
import com.nxg.mvvm.BaseViewModelApplication
import dagger.hilt.android.HiltAndroidApp
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@HiltAndroidApp
class App : BaseViewModelApplication() {

    companion object {
        const val TAG = "AppApplication"
        private var INSTANCE: App by NotNullSingleValueVar()
        fun instance() = INSTANCE
    }

    //定义一个属性管理类，进行非空和重复赋值的判断
    private class NotNullSingleValueVar<T> : ReadWriteProperty<Any?, T> {
        private var value: T? = null
        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return value ?: throw IllegalStateException("application not initialized")
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            this.value = if (this.value == null) value
            else throw IllegalStateException("application already initialized")
        }
    }


    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        Utils.init(this)
        LogUtil.enable = BuildConfig.DEBUG
        registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
    }


    override fun onLowMemory() {
        super.onLowMemory()
        Log.i(
            TAG,
            "onLowMemory: "
        )
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Log.i(
            TAG,
            "onTrimMemory: "
        )
    }

    private val mActivityLifecycleCallbacks: ActivityLifecycleCallbacks =
        object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                Log.i(
                    TAG,
                    "onActivityCreated: " + activity::class.java.name
                )
            }

            override fun onActivityStarted(activity: Activity) {
                Log.i(
                    TAG,
                    "onActivityStarted: " + activity::class.java.name
                )
            }

            override fun onActivityResumed(activity: Activity) {
                Log.i(
                    TAG,
                    "onActivityResumed: " + activity::class.java.name
                )
            }

            override fun onActivityPaused(activity: Activity) {
                Log.i(
                    TAG,
                    "onActivityPaused: " + activity::class.java.name
                )
            }

            override fun onActivityStopped(activity: Activity) {
                Log.i(
                    TAG,
                    "onActivityStopped: " + activity::class.java.name
                )
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                Log.i(
                    TAG,
                    "onActivitySaveInstanceState: " + activity::class.java.name
                )
            }

            override fun onActivityDestroyed(activity: Activity) {
                Log.i(
                    TAG,
                    "onActivityDestroyed: " + activity::class.java.name
                )
            }

        }
}