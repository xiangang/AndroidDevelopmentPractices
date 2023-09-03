package com.nxg.commonui.component.icon

import android.annotation.SuppressLint
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nxg.commonui.component.R
import com.nxg.commonui.component.main.PreviewViewModel
import com.nxg.commonui.theme.ColorBackground
import com.nxg.commonui.theme.ColorText

/**
 * 图标Compose组件
 */
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun IconCompose(
    navController: NavController,
    viewModel: PreviewViewModel
) {
    val colorComponentList by viewModel.iconComponentListStateFlow.collectAsState()
    val grouped =
        colorComponentList.groupBy { it.imageVector.root.name }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.nui_component_base_icon)) },
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
        //IconComponentList(navController, grouped)
        IconComponentGrid(colorComponentList)
    }

}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IconComponentList(navController: NavController, grouped: Map<String, List<IconComponent>>) {
    LazyColumn(
        modifier = Modifier
            .background(ColorBackground.Primary)
    ) {
        grouped.forEach { (headerName, iconComponents) ->
            stickyHeader {
                IconComponentHeader(headerName)
            }
            item {
                Log.d("CommonUI", "IconComponentList iconComponents -> $iconComponents")
                IconComponentGrid(
                    iconComponents
                )
            }
        }
    }
}

@Composable
fun IconComponentHeader(headerName: String) {
    Log.d("CommonUI", "IconComponentHeader: headerName -> $headerName")
    Text(
        modifier = Modifier
            .background(ColorBackground.Primary)
            .fillMaxWidth()
            .padding(10.dp, 0.dp, 0.dp, 0.dp),
        style = MaterialTheme.typography.subtitle1,
        color = ColorText.Primary,
        text = headerName
    )
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun IconComponentGrid(iconComponents: List<IconComponent>) {
    Log.d("CommonUI", "IconComponentGrid: colorComponents -> $iconComponents")
    val clickPosition = remember { mutableStateOf(-1) }
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .background(
                ColorBackground.Primary
            )
            .padding(10.dp, 10.dp, 10.dp, 10.dp)
            .wrapContentHeight()
        //.height(80.dp)//LazyColumn嵌套LazyVerticalGrid，LazyVerticalGrid必须给一个固定的高度，否则 java.lang.IllegalStateException: Vertically scrollable component was measured with an infinity maximum height constraints, which is disallowed. One of the common reasons is nesting layouts like LazyColumn and Column(Modifier.verticalScroll()). If you want to add a header before the list of items please add a header as a separate item() before the main items() inside the LazyColumn scope. There are could be other reasons for this to happen: your ComposeView was added into a LinearLayout with some weight, you applied Modifier.wrapContentSize(unbounded = true) or wrote a custom layout. Please try to remove the source of infinite constraints in the hierarchy above the scrolling container.

    ) {
        items(iconComponents) { iconComponent ->
            IconComponentGridItem(
                iconComponent,
                onClick = { clickPosition.value = iconComponents.indexOf(iconComponent) })
        }
    }
    if (clickPosition.value > -1) {
        AlertDialog(
            backgroundColor = ColorBackground.Primary,
            onDismissRequest = {
                clickPosition.value < 0
            },
            title = {
                Text(if (clickPosition.value > -1) iconComponents[clickPosition.value].imageVector.root.name else "")
            },
            text = {
                Text(if (clickPosition.value > -1) iconComponents[clickPosition.value].imageVector.name else "")
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
fun IconComponentGridItem(iconComponent: IconComponent, onClick: () -> Unit = {}) {
    Log.d("CommonUI", "IconComponentGridItem: iconComponent -> $iconComponent")
    Card(
        modifier = Modifier
            .clickable(enabled = true, onClick = onClick)
            .fillMaxWidth()
            .height(100.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            Icon(
                imageVector = iconComponent.imageVector,
                modifier = Modifier
                    .size(60.dp)
                    .align(Alignment.CenterHorizontally)
                    .padding(10.dp, 10.dp, 10.dp, 10.dp),
                contentDescription = stringResource(id = R.string.nui_component_desc)
            )
            Text(
                modifier = Modifier
                    .wrapContentHeight()
                    .align(Alignment.CenterHorizontally)
                    .padding(10.dp, 0.dp, 10.dp, 10.dp),
                style = MaterialTheme.typography.subtitle1,
                color = ColorText.Primary,
                fontSize = 12.sp,
                text = iconComponent.imageVector.name
            )
        }

    }
}

