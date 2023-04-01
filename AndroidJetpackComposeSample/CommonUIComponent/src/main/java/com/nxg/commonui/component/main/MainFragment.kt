package com.nxg.commonui.component.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nxg.commonui.component.button.ButtonCompose
import com.nxg.commonui.component.cell.CellCompose
import com.nxg.commonui.component.color.ColorCompose
import com.nxg.commonui.component.data.RouteHub
import com.nxg.commonui.component.icon.IconCompose
import com.nxg.commonui.component.image.ImageCompose
import com.nxg.commonui.component.layout.LayoutCompose
import com.nxg.commonui.component.text.TextCompose
import com.nxg.commonui.theme.AndroidJetpackComposeSampleTheme

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var mainViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        return ComposeView(requireContext()).apply {
            setContent {
                AndroidJetpackComposeSampleTheme {
                    NavContent(mainViewModel)
                }
            }
        }
    }

    @Composable
    fun NavContent(mainViewModel: MainViewModel) {
        val navHostController = rememberNavController()
        NavHost(navController = navHostController, startDestination = RouteHub.MAIN) {
            composable(RouteHub.MAIN) {
                MainCompose(
                    navHostController,
                    mainViewModel
                )
            }

            composable(RouteHub.COLOR) {
                ColorCompose(
                    navHostController,
                    mainViewModel
                )
            }

            composable(RouteHub.ICON) {
                IconCompose(
                    navHostController,
                    mainViewModel
                )
            }
            composable(RouteHub.IMAGE) {
                ImageCompose(
                    navHostController,
                    mainViewModel
                )
            }
            composable(RouteHub.BUTTON) {
                ButtonCompose(
                    navHostController,
                    mainViewModel
                )
            }
            composable(RouteHub.TEXT) {
                TextCompose(
                    navHostController,
                    mainViewModel
                )
            }
            composable(RouteHub.LAYOUT) {
                LayoutCompose(
                    navHostController,
                    mainViewModel
                )
            }
            composable(RouteHub.CELL) {
                CellCompose(
                    navHostController,
                    mainViewModel
                )
            }
            composable(RouteHub.BADGE) {
                ColorCompose(
                    navHostController,
                    mainViewModel
                )
            }
            composable(RouteHub.TAG) {
                ColorCompose(
                    navHostController,
                    mainViewModel
                )
            }
            composable(RouteHub.LOADING) {
                ColorCompose(
                    navHostController,
                    mainViewModel
                )
            }
            composable(RouteHub.LOADING_PAGE) {
                ColorCompose(
                    navHostController,
                    mainViewModel
                )
            }

        }
    }

}