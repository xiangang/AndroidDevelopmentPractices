package com.nxg.androidsample.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
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
import com.nxg.commonui.component.R
import com.nxg.commonui.theme.AndroidJetpackComposeSampleTheme
import com.nxg.mvvm.navigation.NavigationDestination
import com.nxg.mvvm.ui.BaseViewModelFragment
import kotlinx.coroutines.launch


/**
 * 主界面
 */
class MainFragment : BaseViewModelFragment() {

    companion object {
        const val TAG = "MainFragment"
    }

    private lateinit var viewModel: MainViewModel

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
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
                        // Screen content
                        MainCompose(
                            viewModel,
                            { navigationDestination -> navigation(navigationDestination) })
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

    private fun navigation(navigationDestination: NavigationDestination) {
        Log.i(TAG, "navigation: navigationDestination $navigationDestination")
        mBaseSharedAndroidViewModel.navigate(navigationDestination)
    }


}