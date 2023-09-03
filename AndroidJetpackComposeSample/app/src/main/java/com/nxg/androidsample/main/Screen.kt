package com.nxg.androidsample.main

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.nxg.androidsample.R

sealed class Screen(@StringRes val nameResourceId: Int, val icon: ImageVector, val route: String) {
    object Chat : Screen(R.string.menu_chat, Icons.Filled.Chat, "chat")
    object Contact : Screen(R.string.menu_contact, Icons.Filled.Contacts, "contact")
    object Discover : Screen(R.string.menu_discovery, Icons.Filled.Explore, "discover")
    object Profile : Screen(R.string.menu_mine, Icons.Filled.Person, "profile")

}