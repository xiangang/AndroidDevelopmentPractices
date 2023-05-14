@file:OptIn(ExperimentalPagerApi::class)

package com.nxg.androidsample.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDirections
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.nxg.androidsample.main.data.Banner
import com.nxg.androidsample.main.data.GridMenu
import com.nxg.androidsample.main.data.NavFunction
import com.nxg.commonui.theme.ColorBackground
import com.nxg.commonui.theme.ColorPrimary
import com.nxg.commonui.theme.ColorText

@Composable
fun HomeCompose(
    viewModel: HomeViewModel,
    onClick: (NavDirections) -> Unit = {}
) {
    HomeNavFunctionList(viewModel, onClick)
}


@Composable
fun HomeBanner(bannerList: List<Banner>) {
    val pagerState = rememberPagerState()
    HorizontalPager(
        modifier = Modifier.background(ColorPrimary.Primary),
        count = bannerList.size
    ) { page ->
        HomeBannerItem(bannerList[page])
    }
    /*HorizontalPagerIndicator(
        pagerState = pagerState,
        modifier = Modifier.padding(16.dp),
    )*/
}

@Composable
fun HomeBannerItem(banner: Banner) {
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
fun HomeHorizontalGridBanner(gridMenuList: List<GridMenu>) {
    val pagerState = rememberPagerState()
    val count =
        if (gridMenuList.size % 10 > 0) gridMenuList.size / 10 + 1 else gridMenuList.size / 10
    HorizontalPager(
        itemSpacing = 10.dp,
        modifier = Modifier.background(
            ColorBackground.Primary
        ),
        count = if (gridMenuList.size % 10 > 0) gridMenuList.size / 10 + 1 else gridMenuList.size / 10
    ) { page ->
        HomeHorizontalGrid(page, gridMenuList)
    }
    /*HorizontalPagerIndicator(
        pagerState = pagerState,
        modifier = Modifier.padding(16.dp),
    )*/
}

@Composable
fun HomeHorizontalGrid(pageIndex: Int, gridMenuList: List<GridMenu>) {
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
            HomeHorizontalGridItem(
                gridMenu
            )
        }
    }
}


@Composable
fun HomeHorizontalGridItem(gridMenu: GridMenu) {
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

@Composable
fun HomeNavFunctionList(
    viewModel: HomeViewModel,
    onClick: (NavDirections) -> Unit = {}
) {
    val banner by viewModel.bannerStateFlow.collectAsState()
    val horizontalGridMenu by viewModel.horizontalGridMenuStateFlow.collectAsState()
    LazyColumn(
        modifier = Modifier
            .background(ColorBackground.Primary)
            .fillMaxSize()
    ) {
        item {
            HomeBanner(banner)
        }
        item {
            HomeHorizontalGridBanner(horizontalGridMenu)
        }
    }
}