package com.nxg.ffmpegstudy.component.entrypoint

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import com.nxg.commonui.theme.*
import com.nxg.ffmpegstudy.component.R
import com.nxg.mvvm.ktx.findMainActivityNavController
import com.nxg.mvvm.ui.BaseViewModelFragment

class AvEntryPointFragment : BaseViewModelFragment() {

    companion object {
        const val TAG = "MainFragment"
    }

    private val viewModel: AvEntryPointViewModel by viewModels()


    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AndroidJetpackComposeSampleTheme {
                    AvContent()
                }

            }
        }

    }

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    fun AvContent() {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(id = R.string.av_app_name)) },
                    navigationIcon = {
                        IconButton(onClick = { }) {
                            Icon(
                                Icons.Filled.ArrowBack,
                                contentDescription = stringResource(id = R.string.av_back)
                            )
                        }
                    }
                )
            }
        ) {
            AvH264Component()
        }
    }

    @Composable
    fun AvHeader(headerName: String) {
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

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun AvH264Component() {
        LazyColumn(
            modifier = Modifier
                .background(ColorBackground.Primary)
                .fillMaxHeight()
        ) {
            stickyHeader {
                AvHeader("H264编解码")
            }
            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .background(
                            ColorBackground.Primary
                        )
                        .padding(10.dp, 10.dp, 10.dp, 10.dp)
                        .height(120.dp)//LazyColumn嵌套LazyVerticalGrid，LazyVerticalGrid必须给一个固定的高度，否则 java.lang.IllegalStateException: Vertically scrollable component was measured with an infinity maximum height constraints, which is disallowed. One of the common reasons is nesting layouts like LazyColumn and Column(Modifier.verticalScroll()). If you want to add a header before the list of items please add a header as a separate item() before the main items() inside the LazyColumn scope. There are could be other reasons for this to happen: your ComposeView was added into a LinearLayout with some weight, you applied Modifier.wrapContentSize(unbounded = true) or wrote a custom layout. Please try to remove the source of infinite constraints in the hierarchy above the scrolling container.

                ) {
                    item {
                        Button(
                            onClick = {
                                findMainActivityNavController().navigate(
                                    AvEntryPointFragmentDirections.avActionEntryPointToH264DecodeStudy()
                                )
                            },
                            shape = MaterialTheme.shapes.small,
                            contentPadding = PaddingValues(
                                start = 10.dp,
                                top = 10.dp,
                                end = 10.dp,
                                bottom = 10.dp
                            ), colors = ButtonDefaults.buttonColors(
                                backgroundColor = ColorPrimary.Primary,
                                contentColor = Color.White
                            )
                        ) {
                            Text(text = "H264解码")
                        }
                    }

                    item {
                        Button(
                            onClick = {
                                findMainActivityNavController().navigate(
                                    AvEntryPointFragmentDirections.avActionEntryPointToH264DecodeStudyCompose()
                                )
                            },
                            shape = MaterialTheme.shapes.small,
                            contentPadding = PaddingValues(
                                start = 10.dp,
                                top = 10.dp,
                                end = 10.dp,
                                bottom = 10.dp
                            ), colors = ButtonDefaults.buttonColors(
                                backgroundColor = ColorPrimary.Primary,
                                contentColor = Color.White
                            )
                        ) {
                            Text(text = "H264解码(Compose)")
                        }
                    }
                }
            }
        }
    }

}