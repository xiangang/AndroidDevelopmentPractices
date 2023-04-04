package com.nxg.androidsample.main

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nxg.androidsample.R
import com.nxg.androidsample.databinding.MainFragmentBinding
import com.nxg.mvvm.ktx.viewBinding
import com.nxg.mvvm.logger.SimpleLogger


/**
 * 主界面
 */
class MainFragment : Fragment(R.layout.main_fragment), SimpleLogger {

    private val binding by viewBinding(MainFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navView: BottomNavigationView = binding.mainFragmentBottomNav
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.main_fragment_nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        Log.i("MainFragment", "onViewCreated: navController $navController")
        navView.setupWithNavController(navController)
    }
}