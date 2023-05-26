package com.nxg.androidsample

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.core.os.bundleOf
import androidx.core.view.WindowCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nxg.androidsample.databinding.FragmentMainBinding
import com.nxg.androidsample.databinding.MainFragmentBinding
import com.nxg.androidsample.main.KtChatViewModel
import com.nxg.im.chat.MainViewModel
import com.nxg.im.chat.databinding.ContentMainBinding
import com.nxg.im.commonui.components.JetchatDrawer
import com.nxg.im.commonui.components.JetchatIcon
import com.nxg.im.commonui.theme.Grey10
import com.nxg.im.commonui.theme.JetchatTheme
import com.nxg.mvvm.logger.SimpleLogger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory


@AndroidEntryPoint
class KtChatShellActivity : AppCompatActivity(), SimpleLogger {

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
                                    this.mainFragmentBottomNav.setupWithNavController(it.navController)
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

    override fun onBackPressed() {
        super.onBackPressed()
        findNavController().popBackStack()
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController().navigateUp() || super.onSupportNavigateUp()
    }

    /**
     * See https://issuetracker.google.com/142847973
     */
    private fun findNavController(): NavController {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.app_nav_host_fragment) as NavHostFragment
        return navHostFragment.navController
    }

}


