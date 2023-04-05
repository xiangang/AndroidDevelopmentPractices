package com.nxg.androidsample.discovery

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavDirections
import com.nxg.androidsample.main.NavFunctionGird
import com.nxg.androidsample.main.NavFunctionHeader
import com.nxg.commonui.component.R
import com.nxg.commonui.theme.AndroidJetpackComposeSampleTheme
import com.nxg.commonui.theme.ColorBackground
import com.nxg.mvvm.ktx.findMainActivityNavController
import com.nxg.mvvm.ui.BaseViewModelFragment

class DiscoveryFragment : BaseViewModelFragment() {

    private val viewModel: DiscoveryViewModel by activityViewModels()

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
                        DiscoveryCompose(
                            viewModel,
                            ::navigation
                        )
                    }

                }
            }
        }
    }


    private fun navigation(navDirection: NavDirections) {
        findMainActivityNavController().navigate(navDirection)
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiscoveryCompose(
    viewModel: DiscoveryViewModel,
    onClick: (NavDirections) -> Unit = {}
) {
    val navFunctionMap by viewModel.navFunctionMapStateFlow.collectAsState()
    LazyColumn(
        modifier = Modifier
            .background(ColorBackground.Primary)
            .fillMaxSize()
    ) {
        navFunctionMap.forEach { (headerName, listNavFunction) ->
            stickyHeader {
                NavFunctionHeader(headerName)
            }
            item {
                NavFunctionGird(
                    listNavFunction,
                    onClick
                )
            }
        }
    }
}
