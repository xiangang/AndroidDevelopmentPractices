package com.nxg.mvvm.ui

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.nxg.mvvm.ktx.findMainActivityNavController

abstract class BaseFragment : Fragment {

    constructor() : super()

    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

}

interface OnBackPressedListener {
    fun onBackPressed()
}