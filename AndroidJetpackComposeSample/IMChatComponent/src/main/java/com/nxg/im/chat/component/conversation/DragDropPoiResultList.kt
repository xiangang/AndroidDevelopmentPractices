// MIT License
//
// Copyright (c) 2022 被风吹过的夏天
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

package com.nxg.im.chat.component.conversation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.amap.api.services.core.PoiItemV2
import com.melody.ui.components.MapPoiItem
import com.nxg.im.commonui.components.EmptyResultText

@Composable
internal fun DragDropPoiResultList(
    poiItemList:  List<PoiItemV2>?,
    onItemClick: (PoiItemV2) -> Unit
) {
    val currentOnItemClick by rememberUpdatedState(newValue = onItemClick)
    Box(modifier = Modifier.fillMaxSize().background(Color(0XFFFAFAFC))) {
        poiItemList?.let { list ->
            if (list.isEmpty()) {
                EmptyResultText(
                    modifier = Modifier
                        .padding(15.dp)
                        .align(Alignment.Center),
                    text = "没有搜索到结果"
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(15.dp)
                ) {
                    items(items = list, key = { it.poiId }) {
                        MapPoiItem(
                            title = it.title,
                            addressName = it.adName,
                            cityName = it.cityName,
                            snippet = it.snippet
                        ) {
                            currentOnItemClick.invoke(it)
                        }
                    }
                }
            }
        }
    }
}