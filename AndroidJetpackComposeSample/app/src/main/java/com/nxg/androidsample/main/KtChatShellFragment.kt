package com.nxg.androidsample.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nxg.androidsample.R
import com.nxg.androidsample.databinding.FragmentMainBinding
import com.nxg.im.commonui.components.JetchatIcon
import com.nxg.im.commonui.theme.JetchatTheme
import com.nxg.mvvm.logger.SimpleLogger
import com.nxg.mvvm.ui.BaseViewModelFragment

/**
 * 暂时启弃用
 *     java.lang.IllegalArgumentException: No view found for id 0x7f0a0064 (com.nxg.androidsample:id/app_nav_host_main_fragment)
 *     for fragment NavHostFragment{8e5a4df} (d245223f-0306-4560-ba58-7849a41b5e80 id=0x7f0a0064)
 */
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
                    Log.i("KtChatShellFragment", "onCreateView: ")
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
                                modifier = Modifier,
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
                        AndroidViewBinding(FragmentMainBinding::inflate) {
                            val navView: BottomNavigationView = this.appBottomNavMainFragment
                            val navHostFragment =
                                childFragmentManager.findFragmentById(R.id.app_nav_host_main_fragment) as NavHostFragment
                            val navController = navHostFragment.navController
                            navController.let {
                                navView.setupWithNavController(navController)
                                navController.addOnDestinationChangedListener { controller, destination, arguments ->
                                    ktChatViewModel.changeTitle(destination.label.toString())
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}