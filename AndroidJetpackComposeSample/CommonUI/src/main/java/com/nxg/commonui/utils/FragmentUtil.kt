package com.nxg.commonui.utils

import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment

fun Fragment.hideSystemBars() {
    requireActivity().hideSystemBars()
}

fun Fragment.showSystemBars() {
    requireActivity().showSystemBars()
}

fun Fragment.showStatusBars() {
    requireActivity().apply {
        WindowCompat.getInsetsController(window, window.decorView)
            .show(WindowInsetsCompat.Type.statusBars())
    }
}

fun Fragment.hideNavigationBars() {
    requireActivity().apply {
        WindowCompat.getInsetsController(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.navigationBars())
        }
    }

}


fun Fragment.setDecorFitsSystemWindows() {
    requireActivity().apply {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }
}