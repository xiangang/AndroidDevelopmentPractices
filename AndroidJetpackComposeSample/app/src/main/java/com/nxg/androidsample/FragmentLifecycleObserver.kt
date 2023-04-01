package com.nxg.androidsample

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.nxg.androidsample.main.AppFragmentLifecycleCallbacks

class FragmentLifecycleObserver(private val supportFragmentManager: FragmentManager) :
    DefaultLifecycleObserver {

    private val mFragmentLifecycleCallbacks: AppFragmentLifecycleCallbacks =
        AppFragmentLifecycleCallbacks()

    override fun onCreate(owner: LifecycleOwner) {

        supportFragmentManager.registerFragmentLifecycleCallbacks(
            mFragmentLifecycleCallbacks,
            true
        )
    }

    override fun onDestroy(owner: LifecycleOwner) {
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(mFragmentLifecycleCallbacks);
    }
}