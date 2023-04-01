@file:OptIn(ExperimentalPagerApi::class)

package com.nxg.androidsample.main

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.nxg.androidsample.main.MainFragment.Companion.TAG
import com.nxg.androidsample.main.data.Banner
import com.nxg.androidsample.main.data.GridMenu
import com.nxg.androidsample.main.data.NavFunction
import com.nxg.commonui.theme.ColorBackground
import com.nxg.commonui.theme.ColorPrimary
import com.nxg.commonui.theme.ColorText
import com.nxg.mvvm.navigation.NavigationDestination

@Composable
fun MainCompose(
    viewModel: MainViewModel,
    navigation: (navigationDestination: NavigationDestination) -> Unit = { _: NavigationDestination -> }
) {
    MainNavFunctionList(viewModel, navigation)
}


@Composable
fun MainBanner(bannerList: List<Banner>) {
    val pagerState = rememberPagerState()
    HorizontalPager(
        modifier = Modifier.background(ColorPrimary.Primary),
        count = bannerList.size
    ) { page ->
        MainBannerItem(bannerList[page])
    }
    /*HorizontalPagerIndicator(
        pagerState = pagerState,
        modifier = Modifier.padding(16.dp),
    )*/
}

@Composable
fun MainBannerItem(banner: Banner) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        AsyncImage(
            model = banner.url,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
                .height(150.dp)
        )
    }
}

@OptIn(
    ExperimentalPagerApi::class
)
@Composable
fun MainHorizontalGridBanner(gridMenuList: List<GridMenu>) {
    val pagerState = rememberPagerState()
    val count =
        if (gridMenuList.size % 10 > 0) gridMenuList.size / 10 + 1 else gridMenuList.size / 10
    Log.i(TAG, "MainHorizontalGridBanner: coutn $count")
    HorizontalPager(
        itemSpacing = 10.dp,
        modifier = Modifier.background(
            ColorBackground.Primary
        ),
        count = if (gridMenuList.size % 10 > 0) gridMenuList.size / 10 + 1 else gridMenuList.size / 10
    ) { page ->
        MainHorizontalGrid(page, gridMenuList)
    }
    /*HorizontalPagerIndicator(
        pagerState = pagerState,
        modifier = Modifier.padding(16.dp),
    )*/
}

@Composable
fun MainHorizontalGrid(pageIndex: Int, gridMenuList: List<GridMenu>) {
    val gridMenuListAtPage = gridMenuList.subList(
        pageIndex * 10,
        if ((pageIndex + 1) * 10 > gridMenuList.size) gridMenuList.size else (pageIndex + 1) * 10
    )
    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .background(
                ColorBackground.Primary
            )
            .padding(10.dp, 0.dp, 0.dp, 0.dp)
            .height(120.dp)
    ) {
        items(
            items = gridMenuListAtPage,/* span = {
                // LazyGridItemSpanScope:
                // maxLineSpan
                GridItemSpan(10)
            }*/
        ) { gridMenu ->
            MainHorizontalGridItem(
                gridMenu,
                onClick = { })
        }
    }
    /* LazyHorizontalGrid(
         rows = GridCells.Fixed(2),
         horizontalArrangement = Arrangement.spacedBy(10.dp),
         verticalArrangement = Arrangement.spacedBy(10.dp),
         modifier = Modifier
             .background(
                 ColorBackground.Primary
             )
             .height(180.dp)
             .wrapContentWidth()
     ) {
         items(
             items = gridMenuListAtPage, span = {
                 // LazyGridItemSpanScope:
                 // maxLineSpan
                 GridItemSpan(10)
             }
         ) { gridMenu ->
             MainHorizontalGridItem(
                 gridMenu,
                 onClick = { })
         }
     }*/
}


@Composable
fun MainHorizontalGridItem(gridMenu: GridMenu, onClick: () -> Unit = {}) {
    Column(
        Modifier
            .fillMaxSize()
            .background(
                ColorBackground.Primary
            )
            .padding(10.dp, 0.dp, 0.dp, 0.dp),
    ) {
        AsyncImage(
            model = gridMenu.icon,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(40.dp)
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.subtitle1,
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            text = gridMenu.title
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainNavFunctionList(
    viewModel: MainViewModel,
    navigation: (navigationDestination: NavigationDestination) -> Unit = { _: NavigationDestination -> }
) {
    val navFunctionMap by viewModel.navFunctionMapStateFlow.collectAsState()
    val banner by viewModel.bannerStateFlow.collectAsState()
    val horizontalGridMenu by viewModel.horizontalGridMenuStateFlow.collectAsState()
    LazyColumn(
        modifier = Modifier
            .background(ColorBackground.Primary)
            .fillMaxSize()
    ) {
        item {
            MainBanner(banner)
        }
        item {
            MainHorizontalGridBanner(horizontalGridMenu)
        }
        navFunctionMap.forEach { (headerName, listNavFunction) ->
            stickyHeader {
                NavFunctionHeader(headerName)
            }
            item {
                NavFunctionHeaderGird(
                    listNavFunction,
                    navigation
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
fun NavFunctionHeaderGird(
    navFunctionList: List<NavFunction>,
    navigation: (navigationDestination: NavigationDestination) -> Unit = { _: NavigationDestination -> }
) {
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
        items(navFunctionList) { navFunction ->
            NavFunctionListItem(
                navFunction,
                onClick = {
                    navigation(navFunction.navigationDestination)
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
            .height(80.dp)
    ) {
        Column(
            Modifier
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
    /*Card(
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
                    .size(60.dp),
                contentScale = ContentScale.FillBounds,
                painter = painterResource(id = navFunction.functionIconResId),
                contentDescription = null,
            )
            Column(
                Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier.fillMaxHeight(),
                    style = MaterialTheme.typography.subtitle1,
                    text = navFunction.functionName
                )
                Text(
                    modifier = Modifier.fillMaxHeight(),
                    style = MaterialTheme.typography.subtitle1,
                    text = navFunction.functionDesc
                )
            }
        }

    }*/
}