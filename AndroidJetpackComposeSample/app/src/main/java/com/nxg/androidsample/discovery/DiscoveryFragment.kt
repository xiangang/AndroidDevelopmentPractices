package com.nxg.androidsample.discovery

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavDirections
import com.nxg.androidsample.main.data.NavFunction
import com.nxg.commonui.component.R
import com.nxg.commonui.theme.AndroidJetpackComposeSampleTheme
import com.nxg.commonui.theme.ColorBackground
import com.nxg.commonui.theme.ColorText
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


@Composable
fun NavFunctionHeader(headerName: String) {
    Text(
        modifier = Modifier
            .background(ColorBackground.Primary)
            .fillMaxWidth()
            .padding(10.dp, 10.dp, 0.dp, 0.dp),
        style = MaterialTheme.typography.subtitle1,
        color = ColorText.Primary,
        text = headerName
    )
}

@Composable
fun NavFunctionGird(
    navFunctionList: List<NavFunction>,
    onClick: (NavDirections) -> Unit = {}
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .background(
                ColorBackground.Primary
            )
            .padding(10.dp, 20.dp, 10.dp, 10.dp)
            .height(90.dp)//LazyColumn嵌套LazyVerticalGrid，LazyVerticalGrid必须给一个固定的高度，否则 java.lang.IllegalStateException: Vertically scrollable component was measured with an infinity maximum height constraints, which is disallowed. One of the common reasons is nesting layouts like LazyColumn and Column(Modifier.verticalScroll()). If you want to add a header before the list of items please add a header as a separate item() before the main items() inside the LazyColumn scope. There are could be other reasons for this to happen: your ComposeView was added into a LinearLayout with some weight, you applied Modifier.wrapContentSize(unbounded = true) or wrote a custom layout. Please try to remove the source of infinite constraints in the hierarchy above the scrolling container.

    ) {
        items(navFunctionList) { navFunction ->
            NavFunctionListItem(
                navFunction,
                onClick = {
                    onClick(navFunction.direction)
                }
            )
        }
    }
}

@Composable
fun NavFunctionListItem(navFunction: NavFunction, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .clickable(enabled = true, onClick = onClick)
            .fillMaxWidth()
            .fillMaxHeight()
            .height(90.dp)
    ) {
        Column(
            Modifier
                .background(
                    ColorBackground.E5E9F2
                )
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(10.dp, 10.dp, 10.dp, 10.dp),
                style = MaterialTheme.typography.subtitle1,
                fontSize = 14.sp,
                text = navFunction.functionName
            )
        }

    }
}
