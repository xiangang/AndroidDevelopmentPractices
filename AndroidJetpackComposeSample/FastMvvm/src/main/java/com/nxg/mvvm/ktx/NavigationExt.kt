package com.nxg.mvvm.ktx

import android.app.Activity
import androidx.annotation.MainThread
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.nxg.mvvm.R
import com.nxg.mvvm.ui.BaseFragment

@MainThread
fun BaseFragment.findMainActivityNavController(activity: Activity = requireActivity()): NavController {
    return Navigation.findNavController(
        activity,
        R.id.app_nav_host_fragment
    )
}