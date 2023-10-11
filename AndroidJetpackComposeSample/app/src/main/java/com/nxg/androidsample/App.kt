package com.nxg.androidsample

import android.app.Activity
import android.os.Bundle
import com.blankj.utilcode.util.Utils
import com.didichuxing.doraemonkit.DoKit
import com.nxg.commonlib.utils.SDKUtils
import com.nxg.im.core.IMClient
import com.nxg.mvvm.BaseViewModelApplication
import com.nxg.mvvm.logger.SimpleLogger
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@HiltAndroidApp
class App : BaseViewModelApplication(), SimpleLogger,
    CoroutineScope by CoroutineScope(Dispatchers.Default) {

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
        SDKUtils.init(this)
        DoKit.Builder(this)
            .build()
        IMClient.init(this)
        registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
    }


    override fun onLowMemory() {
        super.onLowMemory()
        logger.debug { "onLowMemory" }
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        logger.debug { "onTrimMemory" }
    }

    private val mActivityLifecycleCallbacks: ActivityLifecycleCallbacks =
        object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                logger.debug {
                    "onActivityCreated: " + activity::class.java.name
                }
            }

            override fun onActivityStarted(activity: Activity) {
                logger.debug {
                    "onActivityStarted: " + activity::class.java.name
                }
            }

            override fun onActivityResumed(activity: Activity) {
                logger.debug {
                    "onActivityResumed: " + activity::class.java.name
                }
            }

            override fun onActivityPaused(activity: Activity) {
                logger.debug {
                    "onActivityPaused: " + activity::class.java.name
                }
            }

            override fun onActivityStopped(activity: Activity) {
                logger.debug {
                    "onActivityStopped: " + activity::class.java.name
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                logger.debug {
                    "onActivitySaveInstanceState: " + activity::class.java.name
                }
            }

            override fun onActivityDestroyed(activity: Activity) {
                logger.debug {
                    "onActivityDestroyed: " + activity::class.java.name
                }
            }

        }
}