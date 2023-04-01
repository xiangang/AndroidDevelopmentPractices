package com.nxg.androidsample

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.nxg.androidsample.databinding.MainActivityBinding
import com.nxg.androidsample.main.MainShareViewModel
import com.nxg.commonui.utils.setAndroidNativeLightStatusBar
import com.nxg.commonui.utils.transparentStatusBar
import com.nxg.mvvm.applicationViewModels
import com.nxg.mvvm.navigation.NavigationDestination
import com.nxg.mvvm.ui.BaseViewModelActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : BaseViewModelActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    private lateinit var binding: MainActivityBinding
    private lateinit var fragmentLifecycleObserver: FragmentLifecycleObserver
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val appShareViewModel: AppShareViewModel by applicationViewModels()

    //作用域范围为Activity的共享ShareViewModel
    private val mainShareViewModel: MainShareViewModel by viewModels()

    @SuppressLint("RestrictedApi", "ShowToast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        transparentStatusBar(getColor(com.nxg.commonui.R.color.common_ui_primary))
        setAndroidNativeLightStatusBar(true)

        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        /**
         * 使用 FragmentContainerView 创建 NavHostFragment，或通过 FragmentTransaction 手动将 NavHostFragment 添加到您的 activity 时，
         * 尝试通过 Navigation.findNavController(Activity, @IdRes int) 检索 activity 的 onCreate() 中的 NavController 将失败。
         * 您应改为直接从 NavHostFragment 检索 NavController。
         */
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        findViewById<Toolbar>(R.id.toolbar).setupWithNavController(
            navController,
            appBarConfiguration
        )

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                appShareViewModel.uiState.collect {
                    // New value received
                    when (it) {
                        AppShareViewModel.UiState.PERMISSION -> {
                            navHostFragment.navController.navigate(R.id.action_permissionsFragment_to_mainFragment)
                        }
                        AppShareViewModel.UiState.MAIN -> {
                            navHostFragment.navController.navigate(R.id.mainFragment)
                        }
                        AppShareViewModel.UiState.NUI -> {
                            navHostFragment.navController.navigate(R.id.action_mainFragment_to_nui_graph)
                        }

                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mBaseSharedAndroidViewModel.navigationDestination.collect {
                    // New value received
                    Log.i(TAG, "onCreate: navigationDestination  $it")
                    when (it) {
                        NavigationDestination.PERMISSION -> {
                            navHostFragment.navController.navigate(R.id.action_mainFragment_to_permissionsFragment)
                        }
                        NavigationDestination.NUI -> {
                            navHostFragment.navController.navigate(R.id.action_mainFragment_to_nui_graph)
                        }
                        else -> {}
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mBaseSharedAndroidViewModel.navigationDestinationId.collect {
                    //navHostFragment.navController.navigate(it)
                }
            }
        }
        navController.addOnDestinationChangedListener { _, destination, _ ->
            Log.i(TAG, "onCreate: destination changed $destination")
            if (destination.id == R.id.mainFragment) {
                mBaseSharedAndroidViewModel.navigate(NavigationDestination.MAIN)
                transparentStatusBar(getColor(com.nxg.commonui.R.color.common_ui_primary))
            }
            if (destination.id == R.id.commonUIShowFragment) {
                transparentStatusBar(getColor(com.nxg.commonui.R.color.common_ui_primary))
            }
        }
        fragmentLifecycleObserver = FragmentLifecycleObserver(supportFragmentManager)
        lifecycle.addObserver(fragmentLifecycleObserver)
    }

    override fun onSupportNavigateUp(): Boolean {
        Log.i(TAG, "onSupportNavigateUp: ")
        return navController.navigateUp(appBarConfiguration)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.i(TAG, "onOptionsItemSelected: ")
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(fragmentLifecycleObserver)
    }

}


