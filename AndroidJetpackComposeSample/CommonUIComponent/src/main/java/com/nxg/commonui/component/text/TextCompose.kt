package com.nxg.commonui.component.text

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nxg.commonui.component.R
import com.nxg.commonui.component.main.PreviewViewModel
import com.nxg.commonui.theme.*
import com.nxg.commonui.theme.SourceHanSansFontFamily.sourceHanSansCn
import com.nxg.commonui.utils.ActivityUtil
import kotlinx.coroutines.InternalCoroutinesApi


/**
 * 文本Compose组件
 */
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun TextCompose(
    navController: NavController,
    viewModel: PreviewViewModel
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.nui_component_base_text)) },
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
        TextComponentList()
    }
}


@InternalCoroutinesApi
@Preview
@Composable
fun PreviewColorCompose() {
    TextComponentList()
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TextComponentList() {
    LazyColumn(
        modifier = Modifier
            .background(ColorBackground.Primary)
    ) {
        stickyHeader {
            TextComponentHeader("基础功能")
        }
        item {
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .background(
                        ColorBackground.Primary
                    )
                    .padding(10.dp, 10.dp, 10.dp, 10.dp)
                    .height(560.dp)//LazyColumn嵌套LazyVerticalGrid，LazyVerticalGrid必须给一个固定的高度，否则 java.lang.IllegalStateException: Vertically scrollable component was measured with an infinity maximum height constraints, which is disallowed. One of the common reasons is nesting layouts like LazyColumn and Column(Modifier.verticalScroll()). If you want to add a header before the list of items please add a header as a separate item() before the main items() inside the LazyColumn scope. There are could be other reasons for this to happen: your ComposeView was added into a LinearLayout with some weight, you applied Modifier.wrapContentSize(unbounded = true) or wrote a custom layout. Please try to remove the source of infinite constraints in the hierarchy above the scrolling container.

            ) {
                item {
                    val modifier = Modifier
                        .background(
                            ColorBackground.Primary
                        )
                        .padding(10.dp, 10.dp, 10.dp, 10.dp)
                    Column {
                        Text(modifier = modifier, text = "Hello World")
                        Text(
                            modifier = modifier,
                            text = stringResource(R.string.nui_component_from_string_resource)
                        )
                        Text(modifier = modifier, text = "修改文字颜色", color = ColorPrimary.Primary)
                        Text(modifier = modifier, text = "修改字号", fontSize = 30.sp)
                        Text(modifier = modifier, text = "文字设为斜体", fontStyle = FontStyle.Italic)
                        Text(modifier = modifier, text = "文字设为粗体", fontWeight = FontWeight.Bold)
                        Text(
                            modifier = modifier
                                .width(150.dp)
                                .background(ColorBackground.CCFBFF),
                            text = "文字居中对齐",
                            textAlign = TextAlign.Center
                        )
                        Text(
                            modifier = modifier
                                .width(150.dp)
                                .background(ColorBackground.EF96C5),
                            text = "文字左对齐",
                            textAlign = TextAlign.Start
                        )
                        Text(
                            modifier = modifier
                                .width(150.dp)
                                .background(ColorBackground.A0F1EA),
                            text = "文字右对齐",
                            textAlign = TextAlign.End,
                        )

                        Text(
                            modifier = modifier,
                            text = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        color = Color.Blue,
                                        fontStyle = FontStyle.Italic
                                    )
                                ) {
                                    append("蓝色斜体")
                                }
                                append("黑色正常")
                                withStyle(
                                    style = SpanStyle(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Red
                                    )
                                ) {
                                    append("红色加粗")
                                }
                            }
                        )
                        Text(modifier = modifier, text = "最多两行 ".repeat(50), maxLines = 2)
                        Text(
                            modifier = modifier,
                            text = "超出省略号结尾 ".repeat(50),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                    }
                }
            }

        }

        stickyHeader {
            TextComponentHeader("自定义字体")
        }
        item {
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .background(
                        ColorBackground.Primary
                    )
                    .padding(10.dp, 0.dp, 10.dp, 10.dp)
                    .height(360.dp)//LazyColumn嵌套LazyVerticalGrid，LazyVerticalGrid必须给一个固定的高度，否则 java.lang.IllegalStateException: Vertically scrollable component was measured with an infinity maximum height constraints, which is disallowed. One of the common reasons is nesting layouts like LazyColumn and Column(Modifier.verticalScroll()). If you want to add a header before the list of items please add a header as a separate item() before the main items() inside the LazyColumn scope. There are could be other reasons for this to happen: your ComposeView was added into a LinearLayout with some weight, you applied Modifier.wrapContentSize(unbounded = true) or wrote a custom layout. Please try to remove the source of infinite constraints in the hierarchy above the scrolling container.

            ) {
                item {
                    val modifier = Modifier
                        .background(
                            ColorBackground.Primary
                        )
                        .padding(10.dp, 10.dp, 10.dp, 0.dp)
                    Column {
                        Text(
                            modifier = modifier,
                            text = "内置Serif字体",
                            fontFamily = FontFamily.Serif
                        )
                        Text(
                            modifier = modifier,
                            text = "内置SansSerif字体",
                            fontFamily = FontFamily.SansSerif
                        )
                        Text(
                            modifier = modifier,
                            text = "内置Monospace字体",
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            modifier = modifier,
                            text = "内置Cursive字体",
                            fontFamily = FontFamily.Cursive
                        )
                        Text(
                            modifier = modifier,
                            text = "思源字体-Light",
                            fontFamily = sourceHanSansCn,
                            fontWeight = FontWeight.Light,
                            lineHeight = 5.sp
                        )
                        Text(
                            modifier = modifier,
                            fontFamily = sourceHanSansCn,
                            text = "思源字体-Normal",
                            fontWeight = FontWeight.Normal
                        )
                        Text(
                            modifier = modifier,
                            text = "思源字体-Medium",
                            fontFamily = sourceHanSansCn,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            modifier = modifier,
                            text = "思源字体-Bold",
                            fontFamily = sourceHanSansCn,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

        }
        stickyHeader {
            TextComponentHeader("设置主题")
        }
        item {
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .background(
                        ColorBackground.Primary
                    )
                    .padding(10.dp, 10.dp, 10.dp, 10.dp)
                    .height(40.dp)//LazyColumn嵌套LazyVerticalGrid，LazyVerticalGrid必须给一个固定的高度，否则 java.lang.IllegalStateException: Vertically scrollable component was measured with an infinity maximum height constraints, which is disallowed. One of the common reasons is nesting layouts like LazyColumn and Column(Modifier.verticalScroll()). If you want to add a header before the list of items please add a header as a separate item() before the main items() inside the LazyColumn scope. There are could be other reasons for this to happen: your ComposeView was added into a LinearLayout with some weight, you applied Modifier.wrapContentSize(unbounded = true) or wrote a custom layout. Please try to remove the source of infinite constraints in the hierarchy above the scrolling container.

            ) {
                val modifier = Modifier
                    .padding(10.dp, 10.dp, 10.dp, 10.dp)
                    .fillMaxWidth()
                item {
                    Text(
                        modifier = modifier,
                        text = "主色",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colors.primary
                    )
                }
                item {
                    Text(
                        modifier = modifier,
                        text = "错误",
                        textAlign = TextAlign.Center,
                        color = ColorError.Primary
                    )
                }
                item {
                    Text(
                        modifier = modifier,
                        text = "成功",
                        textAlign = TextAlign.Center,
                        color = ColorSuccess.Primary
                    )
                }
                item {
                    Text(
                        modifier = modifier,
                        text = "警告",
                        textAlign = TextAlign.Center,
                        color = ColorWarn.Primary
                    )
                }
                item {
                    Text(
                        modifier = modifier,
                        text = "信息",
                        textAlign = TextAlign.Center,
                        color = ColorInfo.Primary
                    )
                }
            }

        }

        stickyHeader {
            TextComponentHeader("主题字号")
        }
        item {
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .background(
                        ColorBackground.Primary
                    )
                    .padding(10.dp, 10.dp, 10.dp, 10.dp)
                    .height(780.dp)//LazyColumn嵌套LazyVerticalGrid，LazyVerticalGrid必须给一个固定的高度，否则 java.lang.IllegalStateException: Vertically scrollable component was measured with an infinity maximum height constraints, which is disallowed. One of the common reasons is nesting layouts like LazyColumn and Column(Modifier.verticalScroll()). If you want to add a header before the list of items please add a header as a separate item() before the main items() inside the LazyColumn scope. There are could be other reasons for this to happen: your ComposeView was added into a LinearLayout with some weight, you applied Modifier.wrapContentSize(unbounded = true) or wrote a custom layout. Please try to remove the source of infinite constraints in the hierarchy above the scrolling container.

            ) {
                val modifier = Modifier
                    .background(
                        ColorBackground.Primary
                    )
                    .padding(10.dp, 10.dp, 10.dp, 0.dp)
                item {
                    Text(modifier = modifier, text = "H1", style = MaterialTheme.typography.h1)
                }
                item {
                    Text(modifier = modifier, text = "H2", style = MaterialTheme.typography.h2)
                }
                item {
                    Text(modifier = modifier, text = "H3", style = MaterialTheme.typography.h3)
                }
                item {
                    Text(modifier = modifier, text = "H4", style = MaterialTheme.typography.h4)
                }
                item {
                    Text(modifier = modifier, text = "H5", style = MaterialTheme.typography.h5)
                }
                item {
                    Text(modifier = modifier, text = "H6", style = MaterialTheme.typography.h6)
                }
                item {
                    Text(
                        modifier = modifier,
                        text = "Subtitle1",
                        style = MaterialTheme.typography.subtitle1
                    )
                }
                item {
                    Text(
                        modifier = modifier,
                        text = "Subtitle2",
                        style = MaterialTheme.typography.subtitle2
                    )
                }
                item {
                    Text(
                        modifier = modifier,
                        text = "Body1",
                        style = MaterialTheme.typography.body1
                    )
                }
                item {
                    Text(
                        modifier = modifier,
                        text = "Body2",
                        style = MaterialTheme.typography.body2
                    )
                }
                item {
                    Text(
                        modifier = modifier,
                        text = "Button",
                        style = MaterialTheme.typography.button
                    )
                }
                item {
                    Text(
                        modifier = modifier,
                        text = "Caption",
                        style = MaterialTheme.typography.caption
                    )
                }
                item {
                    Text(
                        modifier = modifier,
                        text = "OverLine",
                        style = MaterialTheme.typography.overline
                    )
                }
            }

        }

        stickyHeader {
            TextComponentHeader("拨打电话")
        }
        item {
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .background(
                        ColorBackground.Primary
                    )
                    .padding(10.dp, 10.dp, 10.dp, 10.dp)
                    .height(40.dp)//LazyColumn嵌套LazyVerticalGrid，LazyVerticalGrid必须给一个固定的高度，否则 java.lang.IllegalStateException: Vertically scrollable component was measured with an infinity maximum height constraints, which is disallowed. One of the common reasons is nesting layouts like LazyColumn and Column(Modifier.verticalScroll()). If you want to add a header before the list of items please add a header as a separate item() before the main items() inside the LazyColumn scope. There are could be other reasons for this to happen: your ComposeView was added into a LinearLayout with some weight, you applied Modifier.wrapContentSize(unbounded = true) or wrote a custom layout. Please try to remove the source of infinite constraints in the hierarchy above the scrolling container.

            ) {
                item {
                    val annotatedText = buildAnnotatedString {
                        append("18165787448")
                    }
                    val context = LocalContext.current
                    ClickableText(
                        text = annotatedText,
                        onClick = {
                            ActivityUtil.callPhone(
                                context,
                                annotatedText.text
                            )
                        }
                    )
                }
            }

        }

        stickyHeader {
            TextComponentHeader("超链接")
        }
        item {
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .background(
                        ColorBackground.Primary
                    )
                    .padding(10.dp, 10.dp, 10.dp, 10.dp)
                    .height(40.dp)//LazyColumn嵌套LazyVerticalGrid，LazyVerticalGrid必须给一个固定的高度，否则 java.lang.IllegalStateException: Vertically scrollable component was measured with an infinity maximum height constraints, which is disallowed. One of the common reasons is nesting layouts like LazyColumn and Column(Modifier.verticalScroll()). If you want to add a header before the list of items please add a header as a separate item() before the main items() inside the LazyColumn scope. There are could be other reasons for this to happen: your ComposeView was added into a LinearLayout with some weight, you applied Modifier.wrapContentSize(unbounded = true) or wrote a custom layout. Please try to remove the source of infinite constraints in the hierarchy above the scrolling container.

            ) {
                item {
                    val annotatedText = buildAnnotatedString {
                        append("Click ")
                        // We attach this *URL* annotation to the following content
                        // until `pop()` is called
                        pushStringAnnotation(
                            tag = "URL",
                            annotation = "https://translate.google.cn/?sl=zh-CN&tl=en&op=translate&text="
                        )
                        withStyle(
                            style = SpanStyle(
                                color = Color.Blue,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("here")
                        }
                        pop()
                    }
                    val context = LocalContext.current
                    ClickableText(
                        text = annotatedText,
                        onClick = { offset ->
                            // We check if there is an *URL* annotation attached to the text
                            // at the clicked position
                            // If yes, we log its value
                            Log.d("Clicked URL", "offset $offset ")
                            annotatedText.getStringAnnotations(
                                tag = "URL",
                                start = offset,
                                end = offset
                            ).firstOrNull()?.let { annotation ->
                                // If yes, we log its value
                                Log.d("Clicked URL", annotation.item)
                                ActivityUtil.jumpBrowser(
                                    context,
                                    "${annotation.item}$annotatedText"
                                )
                            }
                        }
                    )
                }
            }
        }

        stickyHeader {
            TextComponentHeader("前后图标")
        }
        item {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .background(
                        ColorBackground.Primary
                    )
                    .padding(10.dp, 10.dp, 10.dp, 10.dp)
                    .height(100.dp)//LazyColumn嵌套LazyVerticalGrid，LazyVerticalGrid必须给一个固定的高度，否则 java.lang.IllegalStateException: Vertically scrollable component was measured with an infinity maximum height constraints, which is disallowed. One of the common reasons is nesting layouts like LazyColumn and Column(Modifier.verticalScroll()). If you want to add a header before the list of items please add a header as a separate item() before the main items() inside the LazyColumn scope. There are could be other reasons for this to happen: your ComposeView was added into a LinearLayout with some weight, you applied Modifier.wrapContentSize(unbounded = true) or wrote a custom layout. Please try to remove the source of infinite constraints in the hierarchy above the scrolling container.

            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Icon(
                            Icons.Filled.GTranslate,
                            contentDescription = null
                        )
                        Text("Google Translate")
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End

                    ) {
                        Text("查看更多")
                        Icon(
                            Icons.Filled.ArrowRightAlt,
                            contentDescription = null
                        )
                    }
                }
            }

        }
    }
}

@Composable
fun TextComponentHeader(headerName: String) {
    Log.d("CommonUI", "TextComponentHeader: headerName -> $headerName")
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
