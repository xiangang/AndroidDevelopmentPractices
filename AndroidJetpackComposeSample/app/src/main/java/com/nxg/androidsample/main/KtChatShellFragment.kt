package com.nxg.androidsample.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nxg.androidsample.R
import com.nxg.androidsample.databinding.MainFragmentBinding
import com.nxg.im.commonui.components.JetchatIcon
import com.nxg.im.commonui.theme.JetchatTheme
import com.nxg.mvvm.logger.SimpleLogger
import com.nxg.mvvm.ui.BaseViewModelFragment

sealed class Screen(val route: String, @StringRes val resourceId: Int) {
    object Conservation : Screen("conversation", R.string.menu_chat)
    object Contact : Screen("Contact", R.string.menu_contact)
    object Discover : Screen("discover", R.string.menu_discovery)
    object Profile : Screen("profile", R.string.menu_mine)
}

class KtChatShellFragment : BaseViewModelFragment(), SimpleLogger {

    private val ktChatViewModel: KtChatViewModel by activityViewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                JetchatTheme {
                    val uiState by ktChatViewModel.uiState.collectAsState()
                    Scaffold(
                        topBar = {
                            CenterAlignedTopAppBar(
                                title = {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        androidx.compose.material3.Text(
                                            text = uiState.title,
                                            style = androidx.compose.material3.MaterialTheme.typography.titleMedium
                                        )

                                    }
                                },
                                navigationIcon = {
                                    JetchatIcon(
                                        contentDescription = stringResource(id = R.string.navigation_drawer_open),
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clickable(onClick = {
                                                //TODO
                                            })
                                            .padding(16.dp)
                                    )
                                },
                                actions = {
                                    // Search icon
                                    Icon(
                                        imageVector = Icons.Outlined.Search,
                                        tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier
                                            .clickable(onClick = {
                                                //TODO
                                            })
                                            .padding(horizontal = 12.dp, vertical = 16.dp)
                                            .height(24.dp),
                                        contentDescription = stringResource(id = R.string.search)
                                    )
                                    // Info icon
                                    Icon(
                                        imageVector = Icons.Outlined.Add,
                                        tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier
                                            .clickable(onClick = {
                                                //TODO
                                            })
                                            .padding(horizontal = 12.dp, vertical = 16.dp)
                                            .height(24.dp),
                                        contentDescription = stringResource(id = R.string.add)
                                    )
                                }
                            )
                        }
                    ) {
                        AndroidViewBinding(MainFragmentBinding::inflate) {
                            val navView: BottomNavigationView = this.mainFragmentBottomNav
                            logger.debug { "R.id.main_fragment_nav_host_fragment = " + R.id.main_fragment_nav_host_fragment }
                            logger.debug {
                                "R.id.main_fragment_nav_host_fragment = 0x" + Integer.toHexString(
                                    R.id.main_fragment_nav_host_fragment
                                )
                            }

                            val navHostFragment =
                                childFragmentManager.findFragmentById(R.id.main_fragment_nav_host_fragment) as NavHostFragment
                            navView.setupWithNavController(navHostFragment.navController)
                            navHostFragment.navController.addOnDestinationChangedListener { controller, destination, arguments ->
                                when (destination.id) {
                                    R.id.navigation_home -> {

                                    }

                                    R.id.navigation_contact -> {

                                    }

                                    R.id.navigation_discovery -> {

                                    }

                                    R.id.navigation_mine -> {

                                    }
                                }
                                ktChatViewModel.changeTitle(destination.label.toString())
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * See https://issuetracker.google.com/142847973
     */
    private fun findNavController(): NavController {
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.main_fragment_nav_host_fragment) as NavHostFragment
        return navHostFragment.navController
    }
}