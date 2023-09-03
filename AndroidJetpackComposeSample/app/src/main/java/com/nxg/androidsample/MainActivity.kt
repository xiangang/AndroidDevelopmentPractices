package com.nxg.androidsample

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.nxg.androidsample.databinding.ActivityMainBinding
import com.nxg.im.user.component.login.LoginViewModel
import com.nxg.im.user.component.login.LoginViewModelFactory
import com.nxg.mvvm.logger.SimpleLogger
import com.nxg.mvvm.ui.BaseViewModelActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : BaseViewModelActivity(), SimpleLogger {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fragmentLifecycleObserver: FragmentLifecycleObserver


    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Turn off the decor fitting system windows, which allows us to handle insets,
        // including IME animations
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fragmentLifecycleObserver = FragmentLifecycleObserver(supportFragmentManager)
        lifecycle.addObserver(fragmentLifecycleObserver)
        loginViewModel.loginResult.observe(this,
            Observer { loginResult ->
                loginResult ?: return@Observer
                loginResult.success?.let {
                    findNavController().navigate(
                        resId = R.id.main_fragment,
                        null,
                        navOptions = NavOptions.Builder()
                            .setPopUpTo(R.id.login_fragment, inclusive = true, saveState = false)
                            .build()
                    )
                }
            })

    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(fragmentLifecycleObserver)
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


