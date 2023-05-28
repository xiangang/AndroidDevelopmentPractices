package com.nxg.androidsample.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nxg.androidsample.R
import com.nxg.androidsample.databinding.FragmentMainBinding
import com.nxg.mvvm.logger.SimpleLogger


/**
 * 主界面
 */
class MainFragment : Fragment(), SimpleLogger {

    private var _binding: FragmentMainBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val navViewNested: BottomNavigationView = binding.appBottomNavMainFragment
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.app_nav_host_main_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        navViewNested.setupWithNavController(navController)
        return root
    }
}