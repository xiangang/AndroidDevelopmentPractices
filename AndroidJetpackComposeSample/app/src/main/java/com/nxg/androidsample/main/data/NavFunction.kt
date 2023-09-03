package com.nxg.androidsample.main.data

import androidx.navigation.NavDirections

class NavFunction constructor(
    var functionGroupName: String,
    val functionName: String,
    val functionIconResId: Int,
    val functionDesc: String = "",
    val direction: NavDirections
)