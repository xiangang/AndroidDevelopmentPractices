/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nxg.im.chat.component.conversation

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.LatLng
import com.melody.map.gd_compose.GDMap
import com.melody.map.gd_compose.poperties.MapUiSettings
import com.melody.map.gd_compose.position.rememberCameraPositionState
import com.nxg.im.commonui.theme.JetchatTheme
import com.nxg.im.core.IMClient
import com.nxg.im.core.module.map.MapService
import com.nxg.mvvm.logger.SimpleLogger
import com.nxg.mvvm.ui.BaseBusinessFragment
import kotlinx.coroutines.launch

class ConversationChatFragment : BaseBusinessFragment(), SimpleLogger {

    private val conversationChatViewModel: ConversationChatViewModel by activityViewModels()

    private val safeArgs: ConversationChatFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(inflater.context).apply {

        setContent {
            JetchatTheme {
                NavContent(conversationChatViewModel)
            }
        }
    }


    @Composable
    fun NavContent(conversationChatViewModel: ConversationChatViewModel) {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "chat") {
            composable("chat") {
                KtChatConversationContent(
                    conversationChatViewModel,
                    onAuthorClick = { user ->
                        // Click callback

                    },
                    resend = { message ->
                        conversationChatViewModel.resendMessage(message)

                    },
                    onMessageSent = {
                        conversationChatViewModel.sendChatTextMessage(it)
                    },
                    onNavigateUp = {
                        findNavController().navigateUp()
                    }, onSelectorChange = {
                        when (it) {
                            InputSelector.MAP -> {
                                navController.navigate("location")
                            }

                            else -> {}
                        }
                    }
                )
            }

            composable("location") {
                DragDropSelectPointScreen(onNavigateUp = {
                    navController.navigateUp()
                })
                //AMapScreen(navController)
            }
        }
    }

    @Composable
    private fun AMapScreen(navController: NavHostController) {
        val mapUiState by IMClient.getService<MapService>().getMapUiStateFlow()
            .collectAsState()
        // 高德地图
        val cameraPositionState = rememberCameraPositionState()
        val scope = rememberCoroutineScope()
        val uiSettings by remember {
            mutableStateOf(
                MapUiSettings(
                    isRotateGesturesEnabled = true,
                    isScrollGesturesEnabled = true,
                    isTiltGesturesEnabled = true,
                    isZoomGesturesEnabled = true,
                    isZoomEnabled = true,
                    isCompassEnabled = true,
                    myLocationButtonEnabled = true,
                    isScaleControlsEnabled = true,
                )
            )
        }
        LaunchedEffect(Unit) {
            //检查权限是否获取
            if (!checkSelfPermissions(requireContext())) {
                requestPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                requestPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            } else {
                //启动一次定位
                IMClient.getService<MapService>().startLocation()
            }
        }

        GDMap(
            modifier = Modifier.fillMaxSize(),
            // 默认提供的位置在：天安门
            cameraPositionState = cameraPositionState,
            uiSettings = uiSettings,
            onMapLoaded = { logger.debug { "onMapLoaded" } },
            onMapClick = { logger.debug { "onMapClick" } },
            onMapLongClick = { logger.debug { "onMapLongClick" } },
            onMapPOIClick = {
                logger.debug { "onMapPOIClick: ${it?.toString()}" }
            },
            onOnMapTouchEvent = {

            },
        )

        LaunchedEffect(mapUiState) {
            launch {
                mapUiState.aMapLocation?.let {
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                it.latitude,
                                it.longitude
                            ), 13F
                        )
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .padding(10.dp, 50.dp, 10.dp, 10.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically,

            ) {

            Row(
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight()
            ) {
                Button(onClick = { navController.navigateUp() }) {
                    Text(text = "取消")
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logger.debug { "chatId: ${safeArgs.chatId}" }
        logger.debug { "chatType: ${safeArgs.chatType}" }
        conversationChatViewModel.insertOrReplaceConversation(safeArgs.chatId, safeArgs.chatType)
        conversationChatViewModel.loadConversationChat(safeArgs.chatId, safeArgs.chatType)
        conversationChatViewModel.getOfflineMessage(safeArgs.chatId.toString())
    }

    override fun doWhenPermissionGranted(permission: String) {
        //启动一次定位
        IMClient.getService<MapService>().startLocation()
    }

}
