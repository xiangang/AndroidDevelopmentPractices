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

class PreviewFragment : Fragment() {

    private lateinit var previewViewModel: PreviewViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        previewViewModel = ViewModelProvider(this)[PreviewViewModel::class.java]
        return ComposeView(requireContext()).apply {
            setContent {
                AndroidJetpackComposeSampleTheme {
                    NavContent(previewViewModel)
                }
            }
        }
    }

    @Composable
    fun NavContent(previewViewModel: PreviewViewModel) {
        val navHostController = rememberNavController()
        NavHost(navController = navHostController, startDestination = RouteHub.MAIN) {
            composable(RouteHub.MAIN) {
                MainCompose(
                    navHostController,
                    previewViewModel
                )
            }

            composable(RouteHub.COLOR) {
                ColorCompose(
                    navHostController,
                    previewViewModel
                )
            }

            composable(RouteHub.ICON) {
                IconCompose(
                    navHostController,
                    previewViewModel
                )
            }
            composable(RouteHub.IMAGE) {
                ImageCompose(
                    navHostController,
                    previewViewModel
                )
            }
            composable(RouteHub.BUTTON) {
                ButtonCompose(
                    navHostController,
                    previewViewModel
                )
            }
            composable(RouteHub.TEXT) {
                TextCompose(
                    navHostController,
                    previewViewModel
                )
            }
            composable(RouteHub.LAYOUT) {
                LayoutCompose(
                    navHostController,
                    previewViewModel
                )
            }
            composable(RouteHub.CELL) {
                CellCompose(
                    navHostController,
                    previewViewModel
                )
            }
            composable(RouteHub.BADGE) {
                ColorCompose(
                    navHostController,
                    previewViewModel
                )
            }
            composable(RouteHub.TAG) {
                ColorCompose(
                    navHostController,
                    previewViewModel
                )
            }
            composable(RouteHub.LOADING) {
                ColorCompose(
                    navHostController,
                    previewViewModel
                )
            }
            composable(RouteHub.LOADING_PAGE) {
                ColorCompose(
                    navHostController,
                    previewViewModel
                )
            }

        }
    }

}