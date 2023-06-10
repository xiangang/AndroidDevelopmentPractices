package com.nxg.androidsample

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.viewModels
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.core.os.bundleOf
import androidx.core.view.WindowCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.nxg.androidsample.databinding.FragmentKtChatShellBinding
import com.nxg.im.chat.component.MainViewModel
import com.nxg.im.commonui.components.JetchatDrawer
import com.nxg.im.user.component.login.LoginViewModel
import com.nxg.im.user.component.login.LoginViewModelFactory
import com.nxg.mvvm.logger.SimpleLogger
import com.nxg.mvvm.ui.BaseViewModelActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class KtChatShellActivity : BaseViewModelActivity(), SimpleLogger {

    private val viewModel: MainViewModel by viewModels()

    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory()
    }


    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Turn off the decor fitting system windows, which allows us to handle insets,
        // including IME animations
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(
            ComposeView(this).apply {
                consumeWindowInsets = false
                setContent {
                    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                    val drawerOpen by viewModel.drawerShouldBeOpened
                        .collectAsStateWithLifecycle()

                    if (drawerOpen) {
                        // Open drawer and reset state in VM.
                        LaunchedEffect(Unit) {
                            // wrap in try-finally to handle interruption whiles opening drawer
                            try {
                                drawerState.open()
                            } finally {
                                viewModel.resetOpenDrawerAction()
                            }
                        }
                    }

                    // Intercepts back navigation when the drawer is open
                    val scope = rememberCoroutineScope()
                    if (drawerState.isOpen) {
                        BackHandler {
                            scope.launch {
                                drawerState.close()
                            }
                        }
                    }

                    JetchatDrawer(
                        drawerState = drawerState,
                        onChatClicked = {
                            //findNavController().popBackStack(R.id.nav_home, false)
                            scope.launch {
                                drawerState.close()
                            }
                        },
                        onProfileClicked = {
                            val bundle = bundleOf("userId" to it)
                            //findNavController().navigate(R.id.nav_profile, bundle)
                            scope.launch {
                                drawerState.close()
                            }
                        }
                    ) {
                        AndroidViewBinding(FragmentKtChatShellBinding::inflate)
                    }
                }
            }
        )
        loginViewModel.loginResult.observe(this,
            Observer { loginResult ->
                loginResult ?: return@Observer
                loginResult.success?.let {
                    findNavController().navigate(
                        resId = R.id.kt_chat_shell_fragment,
                        null,
                        navOptions = NavOptions.Builder()
                            .setPopUpTo(R.id.login_fragment, inclusive = true, saveState = false)
                            .build()
                    )
                }
            })
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


