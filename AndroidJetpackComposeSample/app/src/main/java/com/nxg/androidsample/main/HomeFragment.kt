package com.nxg.androidsample.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import com.nxg.commonui.component.R
import com.nxg.commonui.theme.AndroidJetpackComposeSampleTheme
import com.nxg.mvvm.ktx.findMainActivityNavController
import com.nxg.mvvm.ui.BaseViewModelFragment
import kotlinx.coroutines.launch


/**
 * 首页
 */
class HomeFragment : BaseViewModelFragment() {

    private lateinit var viewModel: HomeViewModel

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        return ComposeView(requireContext()).apply {
            setContent {
                AndroidJetpackComposeSampleTheme {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { Text(stringResource(id = R.string.app_name)) },
                            )
                        }
                    ) {
                        HomeCompose(
                            viewModel,
                            ::navigation
                        )
                    }

                }
            }
        }
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.bannerStateFlow.collect {

            }
        }
    }

    private fun navigation(navDirection: NavDirections) {
        findMainActivityNavController().navigate(navDirection)
    }

}