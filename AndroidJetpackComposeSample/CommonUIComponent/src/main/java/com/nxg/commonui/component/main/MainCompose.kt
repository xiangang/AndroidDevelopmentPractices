package com.nxg.commonui.component.main

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nxg.commonui.component.R
import com.nxg.commonui.component.data.UIComponent
import com.nxg.commonui.component.data.UIComponents
import kotlinx.coroutines.InternalCoroutinesApi

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainCompose(
    navController: NavController,
    viewModel: MainViewModel
) {
    val uiComponentList by viewModel.uiComponentListStateFlow.collectAsState()
    val grouped =
        uiComponentList.groupBy { LocalContext.current.getString(it.componentGroupNameResId) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.nui_component)) },
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
        UIComponentList(navController, grouped)
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UIComponentList(navController: NavController, grouped: Map<String, List<UIComponent>>) {

    LazyColumn(
        modifier = Modifier
            .background(Color.White)
    ) {
        grouped.forEach { (headerName, listUIComponent) ->
            stickyHeader {
                UIComponentHeader(headerName)
            }
            items(listUIComponent) { uiComponent ->
                UIComponentListItem(
                    uiComponent
                ) { navController.navigate(uiComponent.route) }
            }
        }
    }
}

@Composable
fun UIComponentHeader(initial: String) {
    Text(
        modifier = Modifier
            .background(MaterialTheme.colors.primary)
            .fillMaxWidth()
            .padding(10.dp, 10.dp, 10.dp, 10.dp),
        style = MaterialTheme.typography.h6,
        color = Color.White,
        text = initial
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UIComponentListItem(UIComponent: UIComponent, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .padding(10.dp, 10.dp, 10.dp, 0.dp)
            .clickable(enabled = true, onClick = onClick)
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Image(
                modifier = Modifier
                    .size(30.dp)
                    .align(Alignment.CenterVertically),
                contentScale = ContentScale.FillBounds,
                painter = painterResource(id = UIComponent.componentIconResId),
                contentDescription = null,
            )
            Text(
                modifier = Modifier
                    .padding(10.dp, 0.dp)
                    .align(Alignment.CenterVertically)
                    .fillMaxHeight(),
                style = MaterialTheme.typography.subtitle1,
                text = LocalContext.current.getString(UIComponent.componentNameResId)
            )
        }

    }
}

@SuppressLint("MutableCollectionMutableState")
@InternalCoroutinesApi
@Preview()
@Composable
fun PreviewMainCompose() {
    val navHostController = rememberNavController()
    val uiComponentList = UIComponents.uiComponents
    val grouped =
        uiComponentList.groupBy { LocalContext.current.getString(it.componentGroupNameResId) }
    UIComponentList(navHostController, grouped)
}
