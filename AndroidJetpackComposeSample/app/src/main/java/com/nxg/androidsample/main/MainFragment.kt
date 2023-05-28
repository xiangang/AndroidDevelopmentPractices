package com.nxg.androidsample.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nxg.androidsample.R
import com.nxg.androidsample.databinding.FragmentMainBinding
import com.nxg.im.commonui.components.JetchatIcon
import com.nxg.im.commonui.theme.JetchatTheme
import com.nxg.mvvm.logger.SimpleLogger


/**
 * 主界面
 */
class MainFragment : Fragment(), SimpleLogger {

    private val ktChatViewModel: KtChatViewModel by activityViewModels()

    private var _binding: FragmentMainBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val navView: BottomNavigationView = binding.appBottomNavMainFragment
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.app_nav_host_main_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        navView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            ktChatViewModel.changeTitle(destination.label.toString())
        }
        val toolbar: ComposeView = binding.toolbar
        toolbar.setContent {
            JetchatTheme {
                val uiState by ktChatViewModel.uiState.collectAsState()
                CenterAlignedTopAppBar(
                    title = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxHeight()
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxHeight()
                            ) {
                                Text(
                                    text = uiState.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    textAlign = TextAlign.Center
                                )
                            }
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
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (requireActivity() as AppCompatActivity).setSupportActionBar(null)
        _binding = null
    }
}