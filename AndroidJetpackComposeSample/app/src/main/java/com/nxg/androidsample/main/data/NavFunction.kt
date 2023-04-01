package com.nxg.androidsample.main.data

import androidx.annotation.IdRes
import com.nxg.mvvm.navigation.NavigationDestination

class NavFunction(
    var functionGroupName: String,
    val functionName: String,
    val functionIconResId: Int,
    val functionDesc: String = "",
    @IdRes val navigationResId: Int,
    val navigationDestination: NavigationDestination
)