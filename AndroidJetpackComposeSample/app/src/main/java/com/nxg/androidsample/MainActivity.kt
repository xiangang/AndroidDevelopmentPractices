package com.nxg.androidsample

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.nxg.androidsample.databinding.MainActivityBinding
import com.nxg.commonui.utils.isDarkMode
import com.nxg.commonui.utils.setAndroidNativeLightStatusBar
import com.nxg.commonui.utils.transparentStatusBar
import com.nxg.mvvm.logger.SimpleLogger
import com.nxg.mvvm.ui.BaseViewModelActivity
import com.nxg.mvvm.ui.OnBackPressedListener
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : BaseViewModelActivity(), SimpleLogger {

    companion object {
        const val TAG = "MainActivity"
    }

    private lateinit var binding: MainActivityBinding
    private lateinit var fragmentLifecycleObserver: FragmentLifecycleObserver
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isDarkMode()) {
            setAndroidNativeLightStatusBar(false)
        } else {
            transparentStatusBar(getColor(com.nxg.commonui.R.color.common_ui_primary))
        }
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        /**
         * 使用 FragmentContainerView 创建 NavHostFragment，或通过 FragmentTransaction 手动将 NavHostFragment 添加到您的 activity 时，
         * 尝试通过 Navigation.findNavController(Activity, @IdRes int) 检索 activity 的 onCreate() 中的 NavController 将失败。
         * 您应改为直接从 NavHostFragment 检索 NavController。
         */
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.app_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(
            navController,
            appBarConfiguration
        )
        lifecycleScope.launchWhenResumed {
            binding.toolbar.navigationIcon = null
        }
        navController.addOnDestinationChangedListener { _, destination, _ ->
            logger.debug { "addOnDestinationChangedListener: $destination" }
            when (destination.id) {
                R.id.ktChatShellFragment -> {
                    /* val navGraph: NavGraph = navController.graph
                     // 修改startDestination为目标Fragment的id
                     navGraph.setStartDestination(R.id.ktChatShellFragment)
                     // 重新设定NavGraph
                     navController.graph = navGraph*/
                }

                else -> {
                }
            }
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        fragmentLifecycleObserver = FragmentLifecycleObserver(supportFragmentManager)
        //lifecycle.addObserver(fragmentLifecycleObserver)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        logger.debug { "onBackPressed " + findNavController() }
        logger.debug { "onBackPressed " + findNavController().currentDestination?.id }
        logger.debug { "onBackPressed " + findNavController().currentDestination?.navigatorName }
        val fragment: Fragment? =
            supportFragmentManager.findFragmentById(R.id.app_nav_host_fragment)
        logger.debug { "onBackPressed fragment " + fragment?.javaClass?.name }
        if (fragment != null && fragment is OnBackPressedListener) {
            (fragment as OnBackPressedListener).onBackPressed()
        }
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

    override fun onDestroy() {
        super.onDestroy()
        //lifecycle.removeObserver(fragmentLifecycleObserver)
    }

}


