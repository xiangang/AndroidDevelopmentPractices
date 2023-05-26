package com.nxg.androidsample.main

import androidx.annotation.StringRes
import com.nxg.androidsample.R

sealed class Screen(val route: String, @StringRes val resourceId: Int) {
    object Conservation : Screen("conversation", R.string.menu_chat)
    object Contact : Screen("Contact", R.string.menu_contact)
    object Discover : Screen("discover", R.string.menu_discovery)
    object Profile : Screen("profile", R.string.menu_mine)
}