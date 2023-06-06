package com.nxg.androidsample

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.core.view.WindowCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.nxg.androidsample.databinding.FragmentMainBinding
import com.nxg.androidsample.main.KtChatViewModel
import com.nxg.im.chat.component.MainViewModel
import com.nxg.im.commonui.components.JetchatIcon
import com.nxg.im.commonui.theme.JetchatTheme
import com.nxg.mvvm.logger.SimpleLogger
import com.nxg.mvvm.ui.BaseViewModelActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class KtChatShellActivity : BaseViewModelActivity(), SimpleLogger {

    private val viewModel: MainViewModel by viewModels()

    private val ktChatViewModel: KtChatViewModel by viewModels()

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Turn off the decor fitting system windows, which allows us to handle insets,
        // including IME animations
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(
            ComposeView(this).apply {
                //consumeWindowInsets = false
                setContent {
                    JetchatTheme {
                        val uiState by ktChatViewModel.uiState.collectAsState()
                        androidx.compose.material.Scaffold(
                            topBar = {
                                CenterAlignedTopAppBar(
                                    title = {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = uiState.title,
                                                style = MaterialTheme.typography.titleMedium
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
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
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
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
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
                                val navHostFragment =
                                    supportFragmentManager.findFragmentById(R.id.app_nav_host_fragment) as? NavHostFragment
                                navHostFragment?.let {
                                    this.appBottomNavMainFragment.setupWithNavController(it.navController)
                                    it.navController.addOnDestinationChangedListener { _, destination, arguments ->
                                        ktChatViewModel.changeTitle(destination.label.toString())
                                    }
                                }
                            }
                        }
                    }
                }
            }

        )
    }

}


