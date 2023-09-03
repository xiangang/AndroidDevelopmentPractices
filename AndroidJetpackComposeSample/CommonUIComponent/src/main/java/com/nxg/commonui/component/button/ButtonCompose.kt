package com.nxg.commonui.component.button

import android.annotation.SuppressLint
import android.os.Build.VERSION.SDK_INT
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.nxg.commonui.component.R
import com.nxg.commonui.component.color.ColorComponentHeader
import com.nxg.commonui.component.main.PreviewViewModel
import com.nxg.commonui.theme.*
import kotlinx.coroutines.InternalCoroutinesApi


/**
 * 按钮Compose组件
 */
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ButtonCompose(
    navController: NavController,
    viewModel: PreviewViewModel
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.nui_component_base_button)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.nui_component_back)
                        )
                    }
                },
                actions = {
                    // RowScope here, so these icons will be placed horizontally
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(
                            Icons.Filled.Share,
                            contentDescription = stringResource(id = R.string.nui_component_desc)
                        )
                    }
                }
            )
        }
    ) {
        // Screen content
        ButtonComponentList()
    }
}

@InternalCoroutinesApi
@Preview
@Composable
fun PreviewColorCompose() {
    ButtonComponentList()
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ButtonComponentList() {
    LazyColumn(
        modifier = Modifier
            .background(ColorBackground.Primary)
    ) {
        stickyHeader {
            ColorComponentHeader("按钮类型")
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
                        onClick = {},
                        shape = MaterialTheme.shapes.small,
                        contentPadding = PaddingValues(
                            start = 10.dp,
                            top = 10.dp,
                            end = 10.dp,
                            bottom = 10.dp
                        ), colors = ButtonDefaults.buttonColors(
                            backgroundColor = ColorBackground.Primary,
                            contentColor = ColorText.Normal
                        )
                    ) {
                        Text(text = "默认按钮")
                    }
                }

                item {
                    Button(
                        onClick = {},
                        contentPadding = PaddingValues(
                            start = 10.dp,
                            top = 10.dp,
                            end = 10.dp,
                            bottom = 10.dp
                        ), colors = ButtonDefaults.buttonColors(
                            backgroundColor = ColorSuccess.Primary,
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "成功按钮")
                    }
                }

                item {
                    Button(
                        onClick = {},
                        contentPadding = PaddingValues(
                            start = 10.dp,
                            top = 10.dp,
                            end = 10.dp,
                            bottom = 10.dp
                        ), colors = ButtonDefaults.buttonColors(
                            backgroundColor = ColorError.Primary,
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "危险按钮")
                    }
                }


                item {
                    Button(
                        onClick = {},
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
                        Text(text = "主要按钮")
                    }
                }


                item {
                    Button(
                        onClick = {},
                        contentPadding = PaddingValues(
                            start = 10.dp,
                            top = 10.dp,
                            end = 10.dp,
                            bottom = 10.dp
                        ), colors = ButtonDefaults.buttonColors(
                            backgroundColor = ColorWarn.Primary,
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "警告按钮")
                    }
                }
            }
        }

        stickyHeader {
            ColorComponentHeader("镂空按钮")
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
                        onClick = {},
                        shape = MaterialTheme.shapes.small,
                        contentPadding = PaddingValues(
                            start = 10.dp,
                            top = 10.dp,
                            end = 10.dp,
                            bottom = 10.dp
                        ), colors = ButtonDefaults.buttonColors(
                            backgroundColor = ColorBackground.Primary,
                            contentColor = ColorText.Normal
                        )
                    ) {
                        Text(text = "镂空按钮")
                    }
                }

                item {
                    Button(
                        onClick = {},
                        shape = MaterialTheme.shapes.small,
                        contentPadding = PaddingValues(
                            start = 10.dp,
                            top = 10.dp,
                            end = 10.dp,
                            bottom = 10.dp
                        ), colors = ButtonDefaults.buttonColors(
                            backgroundColor = ColorBackground.Primary,
                            contentColor = ColorSuccess.Primary
                        ), border = BorderStroke(0.dp, ColorSuccess.Primary)
                    ) {
                        Text(text = "镂空按钮")
                    }
                }

                item {
                    Button(
                        onClick = {},
                        shape = MaterialTheme.shapes.small,
                        contentPadding = PaddingValues(
                            start = 10.dp,
                            top = 10.dp,
                            end = 10.dp,
                            bottom = 10.dp
                        ), colors = ButtonDefaults.buttonColors(
                            backgroundColor = ColorBackground.Primary,
                            contentColor = ColorError.Primary
                        ), border = BorderStroke(0.dp, ColorError.Primary)
                    ) {
                        Text(text = "镂空按钮")
                    }
                }


                item {
                    Button(
                        onClick = {},
                        shape = MaterialTheme.shapes.small,
                        contentPadding = PaddingValues(
                            start = 10.dp,
                            top = 10.dp,
                            end = 10.dp,
                            bottom = 10.dp
                        ), colors = ButtonDefaults.buttonColors(
                            backgroundColor = ColorBackground.Primary,
                            contentColor = ColorPrimary.Primary
                        ), border = BorderStroke(0.dp, ColorPrimary.Primary)
                    ) {
                        Text(text = "镂空按钮")
                    }
                }


                item {
                    Button(
                        onClick = {},
                        shape = MaterialTheme.shapes.small,
                        contentPadding = PaddingValues(
                            start = 10.dp,
                            top = 10.dp,
                            end = 10.dp,
                            bottom = 10.dp
                        ), colors = ButtonDefaults.buttonColors(
                            backgroundColor = ColorBackground.Primary,
                            contentColor = ColorWarn.Primary
                        ), border = BorderStroke(0.dp, ColorWarn.Primary)
                    ) {
                        Text(text = "镂空按钮")
                    }
                }
            }
        }

        stickyHeader {
            ColorComponentHeader("细边按钮")
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
                        onClick = {},
                        shape = MaterialTheme.shapes.small,
                        contentPadding = PaddingValues(
                            start = 10.dp,
                            top = 10.dp,
                            end = 10.dp,
                            bottom = 10.dp
                        ), colors = ButtonDefaults.buttonColors(
                            backgroundColor = ColorBackground.Primary,
                            contentColor = ColorText.Normal
                        )
                    ) {
                        Text(text = "细边按钮")
                    }
                }

                item {
                    Button(
                        onClick = {},
                        shape = MaterialTheme.shapes.small,
                        contentPadding = PaddingValues(
                            start = 10.dp,
                            top = 10.dp,
                            end = 10.dp,
                            bottom = 10.dp
                        ), colors = ButtonDefaults.buttonColors(
                            backgroundColor = ColorBackground.Primary,
                            contentColor = ColorSuccess.Primary
                        ), border = BorderStroke(0.dp, ColorSuccess.Primary)
                    ) {
                        Text(text = "细边按钮")
                    }
                }

                item {
                    Button(
                        onClick = {},
                        shape = MaterialTheme.shapes.small,
                        contentPadding = PaddingValues(
                            start = 10.dp,
                            top = 10.dp,
                            end = 10.dp,
                            bottom = 10.dp
                        ), colors = ButtonDefaults.buttonColors(
                            backgroundColor = ColorBackground.Primary,
                            contentColor = ColorError.Primary
                        ), border = BorderStroke(0.dp, ColorError.Primary)
                    ) {
                        Text(text = "细边按钮")
                    }
                }


                item {
                    Button(
                        onClick = {},
                        shape = MaterialTheme.shapes.small,
                        contentPadding = PaddingValues(
                            start = 10.dp,
                            top = 10.dp,
                            end = 10.dp,
                            bottom = 10.dp
                        ), colors = ButtonDefaults.buttonColors(
                            backgroundColor = ColorBackground.Primary,
                            contentColor = ColorPrimary.Primary
                        ), border = BorderStroke(0.dp, ColorPrimary.Primary)
                    ) {
                        Text(text = "细边按钮")
                    }
                }


                item {
                    Button(
                        onClick = {},
                        shape = MaterialTheme.shapes.small,
                        contentPadding = PaddingValues(
                            start = 10.dp,
                            top = 10.dp,
                            end = 10.dp,
                            bottom = 10.dp
                        ), colors = ButtonDefaults.buttonColors(
                            backgroundColor = ColorBackground.Primary,
                            contentColor = ColorWarn.Primary
                        ), border = BorderStroke(0.dp, ColorWarn.Primary)
                    ) {
                        Text(text = "细边按钮")
                    }
                }
            }
        }

        stickyHeader {
            ColorComponentHeader("禁用按钮")
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
                        onClick = {},
                        enabled = false,
                        shape = MaterialTheme.shapes.small,
                        contentPadding = PaddingValues(
                            start = 10.dp,
                            top = 10.dp,
                            end = 10.dp,
                            bottom = 10.dp
                        ), colors = ButtonDefaults.buttonColors(
                            backgroundColor = ColorBackground.Primary,
                            contentColor = ColorText.Secondary
                        )
                    ) {
                        Text(text = "禁用按钮")
                    }
                }

                item {
                    Button(
                        onClick = {},
                        enabled = false,
                        contentPadding = PaddingValues(
                            start = 10.dp,
                            top = 10.dp,
                            end = 10.dp,
                            bottom = 10.dp
                        ), colors = ButtonDefaults.buttonColors(
                            backgroundColor = ColorSuccess.Primary,
                            contentColor = Color.White,
                            disabledBackgroundColor = ColorSuccess.Disabled,
                            disabledContentColor = Color.White.copy(alpha = 0.8f),
                        )
                    ) {
                        Text(text = "禁用按钮")
                    }
                }

                item {
                    Button(
                        onClick = {},
                        enabled = false,
                        contentPadding = PaddingValues(
                            start = 10.dp,
                            top = 10.dp,
                            end = 10.dp,
                            bottom = 10.dp
                        ), colors = ButtonDefaults.buttonColors(
                            backgroundColor = ColorError.Primary,
                            contentColor = Color.White,
                            disabledBackgroundColor = ColorError.Disabled,
                            disabledContentColor = Color.White.copy(alpha = 0.8f),
                        )
                    ) {
                        Text(text = "禁用按钮")
                    }
                }


                item {
                    Button(
                        onClick = {},
                        enabled = false,
                        contentPadding = PaddingValues(
                            start = 10.dp,
                            top = 10.dp,
                            end = 10.dp,
                            bottom = 10.dp
                        ), colors = ButtonDefaults.buttonColors(
                            backgroundColor = ColorPrimary.Primary,
                            contentColor = Color.White,
                            disabledBackgroundColor = ColorPrimary.Disabled,
                            disabledContentColor = Color.White.copy(alpha = 0.8f),
                        )
                    ) {
                        Text(text = "禁用按钮")
                    }
                }


                item {
                    Button(
                        onClick = {},
                        enabled = false,
                        contentPadding = PaddingValues(
                            start = 10.dp,
                            top = 10.dp,
                            end = 10.dp,
                            bottom = 10.dp
                        ), colors = ButtonDefaults.buttonColors(
                            backgroundColor = ColorWarn.Primary,
                            contentColor = Color.White,
                            disabledBackgroundColor = ColorWarn.Disabled,
                            disabledContentColor = Color.White.copy(alpha = 0.8f),
                        )
                    ) {
                        Text(text = "禁用按钮")
                    }
                }
            }
        }

        stickyHeader {
            ColorComponentHeader("加载中")
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
                    .height(60.dp)//LazyColumn嵌套LazyVerticalGrid，LazyVerticalGrid必须给一个固定的高度，否则 java.lang.IllegalStateException: Vertically scrollable component was measured with an infinity maximum height constraints, which is disallowed. One of the common reasons is nesting layouts like LazyColumn and Column(Modifier.verticalScroll()). If you want to add a header before the list of items please add a header as a separate item() before the main items() inside the LazyColumn scope. There are could be other reasons for this to happen: your ComposeView was added into a LinearLayout with some weight, you applied Modifier.wrapContentSize(unbounded = true) or wrote a custom layout. Please try to remove the source of infinite constraints in the hierarchy above the scrolling container.

            ) {
                item {
                    Button(
                        onClick = {},
                        contentPadding = PaddingValues(
                            start = 10.dp,
                            top = 10.dp,
                            end = 10.dp,
                            bottom = 10.dp
                        ), colors = ButtonDefaults.buttonColors(
                            backgroundColor = ColorSuccess.Primary,
                            contentColor = Color.White
                        )
                    ) {
                        // Inner content including an icon and a text label
                        val imageLoader = ImageLoader.Builder(LocalContext.current)
                            .components {
                                if (SDK_INT >= 28) {
                                    add(ImageDecoderDecoder.Factory())
                                } else {
                                    add(SvgDecoder.Factory())
                                    add(GifDecoder.Factory())
                                }
                            }
                            .build()
                        val painter = rememberAsyncImagePainter(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(R.drawable.nui_svg_loading)
                                .size(Size.ORIGINAL) // Set the target size to load the image at.
                                .build(),
                            imageLoader = imageLoader
                        )
                        val infiniteTransition = rememberInfiniteTransition()
                        val degrees by infiniteTransition.animateFloat(
                            initialValue = 0f,
                            targetValue = 360f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1000, easing = LinearEasing),
                                repeatMode = RepeatMode.Restart
                            )
                        )
                        Image(
                            painter = painter,
                            modifier = Modifier
                                .size(ButtonDefaults.IconSize)
                                .rotate(degrees),
                            contentDescription = null
                        )
                        /*Icon(
                            Icons.Filled.Favorite,
                            contentDescription = "Favorite",
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )*/
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text("加载中")
                    }
                }

                item {
                    Button(
                        onClick = {},
                        contentPadding = PaddingValues(
                            start = 10.dp,
                            top = 10.dp,
                            end = 10.dp,
                            bottom = 10.dp
                        ), colors = ButtonDefaults.buttonColors(
                            backgroundColor = ColorError.Primary,
                            contentColor = Color.White
                        )
                    ) {
                        // Inner content including an icon and a text label
                        val imageLoader = ImageLoader.Builder(LocalContext.current)
                            .components {
                                if (SDK_INT >= 28) {
                                    add(ImageDecoderDecoder.Factory())
                                } else {
                                    add(SvgDecoder.Factory())
                                    add(GifDecoder.Factory())
                                }
                            }
                            .build()
                        val painter = rememberAsyncImagePainter(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(R.drawable.nui_svg_loading)
                                .size(Size.ORIGINAL) // Set the target size to load the image at.
                                .build(),
                            imageLoader = imageLoader
                        )
                        val infiniteTransition = rememberInfiniteTransition()
                        val degrees by infiniteTransition.animateFloat(
                            initialValue = 0f,
                            targetValue = 360f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1000, easing = LinearEasing),
                                repeatMode = RepeatMode.Restart
                            )
                        )
                        Image(
                            painter = painter,
                            modifier = Modifier
                                .size(ButtonDefaults.IconSize)
                                .rotate(degrees),
                            contentDescription = null
                        )
                        /*Icon(
                            Icons.Filled.Favorite,
                            contentDescription = "Favorite",
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )*/
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text("加载中")
                    }
                }


            }
        }

        stickyHeader {
            ColorComponentHeader("按钮图标&&按钮形状")
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
                    .height(60.dp)//LazyColumn嵌套LazyVerticalGrid，LazyVerticalGrid必须给一个固定的高度，否则 java.lang.IllegalStateException: Vertically scrollable component was measured with an infinity maximum height constraints, which is disallowed. One of the common reasons is nesting layouts like LazyColumn and Column(Modifier.verticalScroll()). If you want to add a header before the list of items please add a header as a separate item() before the main items() inside the LazyColumn scope. There are could be other reasons for this to happen: your ComposeView was added into a LinearLayout with some weight, you applied Modifier.wrapContentSize(unbounded = true) or wrote a custom layout. Please try to remove the source of infinite constraints in the hierarchy above the scrolling container.

            ) {
                item {
                    Button(
                        onClick = {},
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
                        // Inner content including an icon and a text label
                        Icon(
                            Icons.Filled.Favorite,
                            contentDescription = "Favorite",
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text("图标")
                    }
                }

                item {
                    Button(
                        onClick = {},
                        contentPadding = PaddingValues(
                            start = 10.dp,
                            top = 10.dp,
                            end = 10.dp,
                            bottom = 10.dp
                        ), colors = ButtonDefaults.buttonColors(
                            backgroundColor = ColorError.Primary,
                            contentColor = Color.White
                        ), shape = RoundedCornerShape(50.dp)
                    ) {
                        // Inner content including an icon and a text label
                        Icon(
                            Icons.Filled.ThumbUp,
                            contentDescription = "Favorite",
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text("形状")
                    }
                }

            }
        }

        stickyHeader {
            ColorComponentHeader("自定义颜色")
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
                        onClick = {},
                        contentPadding = PaddingValues(
                            start = 10.dp,
                            top = 10.dp,
                            end = 10.dp,
                            bottom = 10.dp
                        ), colors = ButtonDefaults.buttonColors(
                            backgroundColor = ColorSuccess.Primary,
                            contentColor = Color.White
                        )
                    ) {
                        Text("渐变色")
                    }

                }
                item {
                    Button(
                        onClick = {},
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
                        Text("渐变色")
                    }
                }
                item {
                    Button(
                        onClick = {},
                        contentPadding = PaddingValues(
                            start = 10.dp,
                            top = 10.dp,
                            end = 10.dp,
                            bottom = 10.dp
                        ), colors = ButtonDefaults.buttonColors(
                            backgroundColor = Teal200,
                            contentColor = Color.White
                        )
                    ) {
                        Text("青绿色")
                    }
                }

                item {
                    GradientButton(
                        text = "渐变色",
                        gradient = Brush.linearGradient(
                            colors = listOf(
                                ColorBackground.CCFBFF,
                                ColorBackground.EF96C5,
                            )
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp, 10.dp, 8.dp, 10.dp)
                    )
                }

                item {
                    GradientButton(
                        text = "渐变色",
                        gradient = Brush.linearGradient(
                            colors = listOf(
                                ColorBackground.EAD6EE,
                                ColorBackground.A0F1EA,
                            )
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp, 10.dp, 8.dp, 10.dp)
                    )
                }
                item {
                    GradientButton(
                        text = "渐变色",
                        gradient = Brush.linearGradient(
                            colors = listOf(
                                ColorBackground.EEBD89,
                                ColorBackground.D13ABD,
                            )
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp, 10.dp, 8.dp, 10.dp)
                    )
                }
            }
        }


        stickyHeader {
            ColorComponentHeader("自定义大小")
        }

        item {
            Column {
                Button(
                    modifier = Modifier
                        .padding(10.dp, 10.dp, 10.dp, 10.dp)
                        .fillMaxWidth()
                        .height(60.dp),
                    onClick = {},
                    contentPadding = PaddingValues(
                        start = 10.dp,
                        top = 10.dp,
                        end = 10.dp,
                        bottom = 10.dp
                    ), colors = ButtonDefaults.buttonColors(
                        backgroundColor = ColorSuccess.Primary,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "成功按钮", style = MaterialTheme.typography.h6)
                }

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
                            onClick = {},
                            contentPadding = PaddingValues(
                                start = 10.dp,
                                top = 10.dp,
                                end = 10.dp,
                                bottom = 10.dp
                            ), colors = ButtonDefaults.buttonColors(
                                backgroundColor = ColorError.Primary,
                                contentColor = Color.White
                            ),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(text = "危险按钮")
                        }
                    }


                    item {
                        Button(
                            onClick = {},
                            contentPadding = PaddingValues(
                                start = 10.dp,
                                top = 10.dp,
                                end = 10.dp,
                                bottom = 10.dp
                            ), colors = ButtonDefaults.buttonColors(
                                backgroundColor = ColorPrimary.Primary,
                                contentColor = Color.White
                            ),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text(text = "主要按钮")
                        }
                    }


                    item {
                        Button(
                            onClick = {},
                            contentPadding = PaddingValues(
                                start = 10.dp,
                                top = 10.dp,
                                end = 10.dp,
                                bottom = 10.dp
                            ), colors = ButtonDefaults.buttonColors(
                                backgroundColor = ColorWarn.Primary,
                                contentColor = Color.White
                            ),
                            shape = MaterialTheme.shapes.large
                        ) {
                            Text(text = "警告按钮")
                        }
                    }
                }

            }

        }

    }
}


@Composable
fun GradientButton(
    text: String,
    gradient: Brush,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = { },
) {
    Button(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent,
            contentColor = Color.White
        ),
        contentPadding = PaddingValues(),
        onClick = { onClick() },
    ) {
        Box(
            modifier = Modifier
                .background(gradient)
                .then(modifier),
            contentAlignment = Alignment.Center,
        ) {
            Text("渐变色")
        }
    }
}