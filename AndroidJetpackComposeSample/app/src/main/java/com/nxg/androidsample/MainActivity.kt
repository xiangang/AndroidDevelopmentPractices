package com.nxg.androidsample

import android.os.Bundle
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.nxg.androidsample.databinding.MainActivityBinding
import com.nxg.commonui.utils.setAndroidNativeLightStatusBar
import com.nxg.commonui.utils.transparentStatusBar
import com.nxg.mvvm.logger.SimpleLogger
import com.nxg.mvvm.ui.BaseViewModelActivity
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
            supportFragmentManager.findFragmentById(R.id.app_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        Log.i(TAG, "onCreate: navController $navController" )
        appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(
            navController,
            appBarConfiguration
        )
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        fragmentLifecycleObserver = FragmentLifecycleObserver(supportFragmentManager)
        //lifecycle.addObserver(fragmentLifecycleObserver)
    }

//    override fun onSupportNavigateUp(): Boolean {
////        return navController.navigateUp(appBarConfiguration)
//        return false
//    }


    override fun onDestroy() {
        super.onDestroy()
        //lifecycle.removeObserver(fragmentLifecycleObserver)
    }

}


