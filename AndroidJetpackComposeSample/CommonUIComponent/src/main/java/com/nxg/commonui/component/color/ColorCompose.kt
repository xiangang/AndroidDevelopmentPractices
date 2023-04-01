package com.nxg.commonui.component.color

import android.annotation.SuppressLint
import android.text.TextUtils
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nxg.commonui.component.R
import com.nxg.commonui.component.main.MainViewModel
import com.nxg.commonui.theme.ColorBackground
import com.nxg.commonui.theme.ColorPrimary
import com.nxg.commonui.theme.ColorText
import com.nxg.commonui.utils.toHexString
import kotlinx.coroutines.InternalCoroutinesApi

/**
 * 颜色Compose组件
 */
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ColorCompose(
    navController: NavController,
    viewModel: MainViewModel
) {
    val colorComponentList by viewModel.colorComponentListStateFlow.collectAsState()
    val grouped =
        colorComponentList.groupBy { stringResource(it.colorGroupNameResId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.nui_component_base_color)) },
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
        ColorComponentList(navController, grouped)
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ColorComponentList(navController: NavController, grouped: Map<String, List<ColorComponent>>) {
    LazyColumn(
        modifier = Modifier
            .background(ColorBackground.Primary)
    ) {
        grouped.forEach { (headerName, colorComponents) ->
            stickyHeader {
                ColorComponentHeader(headerName)
            }
            item {
                Log.d("CommonUI", "ColorComponentList colorComponents -> $colorComponents")
                ColorComponentGrid(
                    colorComponents
                )
            }
        }
    }
}

@Composable
fun ColorComponentHeader(headerName: String) {
    Log.d("CommonUI", "ColorComponentHeader: headerName -> $headerName")
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

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun ColorComponentGrid(colorComponents: List<ColorComponent>) {
    Log.d("CommonUI", "ColorComponentGridItem: colorComponents -> $colorComponents")
    val clickPosition = remember { mutableStateOf(-1) }
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .background(
                ColorBackground.Primary
            )
            .padding(10.dp, 20.dp, 10.dp, 10.dp)
            .height(80.dp)//LazyColumn嵌套LazyVerticalGrid，LazyVerticalGrid必须给一个固定的高度，否则 java.lang.IllegalStateException: Vertically scrollable component was measured with an infinity maximum height constraints, which is disallowed. One of the common reasons is nesting layouts like LazyColumn and Column(Modifier.verticalScroll()). If you want to add a header before the list of items please add a header as a separate item() before the main items() inside the LazyColumn scope. There are could be other reasons for this to happen: your ComposeView was added into a LinearLayout with some weight, you applied Modifier.wrapContentSize(unbounded = true) or wrote a custom layout. Please try to remove the source of infinite constraints in the hierarchy above the scrolling container.

    ) {
        items(colorComponents) { colorComponent ->
            ColorComponentGridItem(
                colorComponent,
                onClick = { clickPosition.value = colorComponents.indexOf(colorComponent) })
        }
    }
    if (clickPosition.value > -1) {
        AlertDialog(
            backgroundColor = if (clickPosition.value > -1) colorComponents[clickPosition.value].color else Color.White,
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onCloseRequest.
                clickPosition.value < 0
            },
            title = {
                Text(if (clickPosition.value > -1) stringResource(id = colorComponents[clickPosition.value].colorNameResId) else "")
            },
            text = {
                Text(if (clickPosition.value > -1) colorComponents[clickPosition.value].color.toHexString() else "")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        clickPosition.value = -1
                    }
                ) {
                    Text(stringResource(id = R.string.nui_component_confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        clickPosition.value = -1
                    }
                ) {
                    Text(stringResource(id = R.string.nui_component_close))
                }
            }
        )
    }


}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ColorComponentGridItem(colorComponent: ColorComponent, onClick: () -> Unit = {}) {
    Log.d("CommonUI", "ColorComponentGridItem: colorComponent -> $colorComponent")
    Card(
        modifier = Modifier
            .clickable(enabled = true, onClick = onClick)
            .fillMaxWidth()
            .fillMaxHeight(),
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(colorComponent.color)
        ) {
            Text(
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(10.dp, 10.dp, 10.dp, 10.dp),
                style = MaterialTheme.typography.subtitle1,
                color = getTextColor(colorComponent),
                fontSize = 14.sp,
                text = stringResource(colorComponent.colorNameResId)
            )
            Text(
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(10.dp, 0.dp, 10.dp, 10.dp),
                color = getTextColor(colorComponent),
                style = MaterialTheme.typography.h6,
                text = colorComponent.color.toHexString(),
                fontSize = 12.sp
            )
        }

    }
}

@Composable
fun getTextColor(colorComponent: ColorComponent): Color {
    return if (TextUtils.equals(
            stringResource(colorComponent.colorNameResId),
            stringResource(R.string.nui_component_base_color_primary_light)
        ) || TextUtils.equals(
            stringResource(colorComponent.colorNameResId),
            stringResource(R.string.nui_component_base_color_background_primary)
        )
    ) ColorText.Primary else Color.White
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ColorComponentListItem(colorComponent: ColorComponent, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .padding(10.dp, 10.dp, 10.dp, 0.dp)
            .clickable(enabled = true, onClick = onClick)
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {

            Column(
                Modifier
                    .fillMaxWidth()
                    .background(colorComponent.color)
            ) {
                Text(
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(10.dp, 10.dp, 10.dp, 10.dp),
                    style = MaterialTheme.typography.subtitle1,
                    text = stringResource(colorComponent.colorNameResId)
                )
                Text(
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(10.dp, 0.dp, 10.dp, 10.dp),
                    style = MaterialTheme.typography.subtitle1,
                    text = colorComponent.color.toHexString()
                )
            }
        }

    }
}

@InternalCoroutinesApi
@Preview
@Composable
fun PreviewColorCompose() {
    val navHostController = rememberNavController()
    val mainViewModel: MainViewModel = viewModel()
    ColorCompose(navHostController, mainViewModel)
}

@InternalCoroutinesApi
@Preview
@Composable
fun PreviewColorComponentListItem() {

    ColorComponentListItem(
        ColorComponent(
            R.string.nui_component_base_color_primary,
            R.string.nui_component_base_color_primary_primary,
            ColorPrimary.Primary
        )
    )
}
