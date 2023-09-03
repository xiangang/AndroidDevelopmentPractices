package com.nxg.androidsample.main

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.nxg.androidsample.App

class AppFragmentLifecycleCallbacks : FragmentManager.FragmentLifecycleCallbacks() {
    override fun onFragmentPreAttached(
        fm: FragmentManager,
        f: Fragment,
        context: Context
    ) {
        super.onFragmentPreAttached(fm, f, context)
        Log.i(
            App.TAG,
            "onFragmentPreAttached: " + f::class.java.name
        )
    }

    override fun onFragmentActivityCreated(
        fm: FragmentManager,
        f: Fragment,
        savedInstanceState: Bundle?
    ) {
        super.onFragmentActivityCreated(fm, f, savedInstanceState)
        Log.i(
            App.TAG,
            "onFragmentActivityCreated: " + f::class.java.name
        )
    }

    override fun onFragmentAttached(
        fm: FragmentManager,
        f: Fragment,
        context: Context
    ) {
        super.onFragmentAttached(fm, f, context)
        Log.i(App.TAG, "onFragmentAttached: " + f::class.java.name)
    }

    override fun onFragmentPreCreated(
        fm: FragmentManager,
        f: Fragment,
        savedInstanceState: Bundle?
    ) {
        super.onFragmentPreCreated(fm, f, savedInstanceState)
        Log.i(App.TAG, "onFragmentPreCreated: " + f::class.java.name)
    }

    override fun onFragmentCreated(
        fm: FragmentManager,
        f: Fragment,
        savedInstanceState: Bundle?
    ) {
        super.onFragmentCreated(fm, f, savedInstanceState)
        Log.i(App.TAG, "onFragmentCreated: " + f::class.java.name)
    }

    override fun onFragmentViewCreated(
        fm: FragmentManager,
        f: Fragment,
        v: View,
        savedInstanceState: Bundle?
    ) {
        super.onFragmentViewCreated(fm, f, v, savedInstanceState)
        Log.i(
            App.TAG,
            "onFragmentViewCreated: " + f::class.java.name
        )
    }

    override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
        super.onFragmentStarted(fm, f)
        Log.i(App.TAG, "onFragmentStarted: " + f::class.java.name)
    }

    override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
        super.onFragmentResumed(fm, f)
        Log.i(App.TAG, "onFragmentResumed: " + f::class.java.name)
    }

    override fun onFragmentPaused(fm: FragmentManager, f: Fragment) {
        super.onFragmentPaused(fm, f)
        Log.i(App.TAG, "onFragmentPaused: " + f::class.java.name)
    }

    override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
        super.onFragmentStopped(fm, f)
        Log.i(App.TAG, "onFragmentStopped: " + f::class.java.name)
    }

    override fun onFragmentSaveInstanceState(
        fm: FragmentManager,
        f: Fragment,
        outState: Bundle
    ) {
        super.onFragmentSaveInstanceState(fm, f, outState)
        Log.i(
            App.TAG,
            "onFragmentSaveInstanceState: " + f::class.java.name
        )
    }

    override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
        super.onFragmentViewDestroyed(fm, f)
        Log.i(
            App.TAG,
            "onFragmentViewDestroyed: " + f::class.java.name
        )
    }

    override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
        super.onFragmentDestroyed(fm, f)
        Log.i(App.TAG, "onFragmentDestroyed: " + f::class.java.name)
    }

    override fun onFragmentDetached(fm: FragmentManager, f: Fragment) {
        super.onFragmentDetached(fm, f)
        Log.i(App.TAG, "onFragmentDetached: " + f::class.java.name)
    }
}