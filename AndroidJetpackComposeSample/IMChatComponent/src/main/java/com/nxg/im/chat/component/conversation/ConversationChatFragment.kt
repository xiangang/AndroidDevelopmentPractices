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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.LatLng
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.melody.map.gd_compose.GDMap
import com.melody.map.gd_compose.poperties.MapUiSettings
import com.melody.map.gd_compose.position.rememberCameraPositionState
import com.nxg.im.chat.R
import com.nxg.im.chat.component.jetchat.MainViewModel
import com.nxg.im.chat.component.jetchat.data.exampleUiState
import com.nxg.im.chat.component.utils.requestMultiplePermission
import com.nxg.im.commonui.components.coil.CoilImageEngine
import com.nxg.im.commonui.theme.JetchatTheme
import com.nxg.im.core.IMClient
import com.nxg.im.core.module.chat.ChatService
import com.nxg.im.core.module.map.MapService
import com.nxg.im.core.module.upload.UploadService
import com.nxg.mvvm.logger.SimpleLogger
import com.nxg.mvvm.ui.BaseBusinessFragment
import github.leavesczy.matisse.DefaultMediaFilter
import github.leavesczy.matisse.Matisse
import github.leavesczy.matisse.MatisseContract
import github.leavesczy.matisse.MediaResource
import github.leavesczy.matisse.MimeType
import github.leavesczy.matisse.SmartCaptureStrategy
import kotlinx.coroutines.launch

class ConversationChatFragment : BaseBusinessFragment(), SimpleLogger {

    companion object {
        private const val TAG = "ConversationChat"
    }

    private val activityViewModel: MainViewModel by activityViewModels()

    private val conversationChatViewModel: ConversationChatViewModel by activityViewModels()

    private val safeArgs: ConversationChatFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(inflater.context).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        setContent {
            JetchatTheme {
                NavContent(conversationChatViewModel)
            }
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun NavContent(conversationChatViewModel: ConversationChatViewModel) {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "chat") {
            composable("chat") {
                val mediaPickerLauncher =
                    rememberLauncherForActivityResult(contract = MatisseContract()) { result: List<MediaResource> ->
                        if (result.isNotEmpty()) {
                            val mediaResource = result[0]
                            val uri = mediaResource.uri
                            val path = mediaResource.path
                            val name = mediaResource.name
                            val mimeType = mediaResource.mimeType
                            Log.i(TAG, "rememberLauncherForActivityResult: Matisse $mediaResource")
                            conversationChatViewModel.sendChatImageMessage(path)
                        }
                    }

                val onVideoCallSelected = remember { mutableStateOf(false) }
                val requestVideCallPermission = requestMultiplePermission(
                    permissions = listOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO
                    )
                )
                LaunchedEffect(
                    onVideoCallSelected,
                    requestVideCallPermission.allPermissionsGranted
                ) {
                    logger.debug { "requestVideCallPermission.allPermissionsGranted ${requestVideCallPermission.allPermissionsGranted}" }
                    logger.debug { "onVideoCallSelected.value ${onVideoCallSelected.value}" }
                    if (onVideoCallSelected.value) {
                        if (!requestVideCallPermission.allPermissionsGranted) {
                            requestVideCallPermission.launchMultiplePermissionRequest()
                        } else {
                            conversationChatViewModel.uiState.value.conversationChat?.let { chat ->
                                conversationChatViewModel.videoCallService.call(chat.friend.friendId)
                            }
                            onVideoCallSelected.value = false
                        }
                    }
                }
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
                    },
                    onSelectorChange = {
                        when (it) {
                            InputSelector.PICTURE -> {
                                val matisse = Matisse(
                                    maxSelectable = 1,
                                    mediaFilter = DefaultMediaFilter(
                                        supportedMimeTypes = MimeType.ofImage(
                                            hasGif = true
                                        )
                                    ),
                                    imageEngine = CoilImageEngine(),
                                    captureStrategy = SmartCaptureStrategy("com.nxg.androidsample.authority")
                                )
                                mediaPickerLauncher.launch(matisse)
                            }

                            InputSelector.MAP -> {
                                navController.navigate("location")
                            }

                            InputSelector.PHONE -> {
                                onVideoCallSelected.value = true

                                logger.debug { "requestVideCallPermission.allPermissionsGranted ${requestVideCallPermission.allPermissionsGranted}" }
                                if (requestVideCallPermission.allPermissionsGranted) {
                                    conversationChatViewModel.uiState.value.conversationChat?.let { chat ->
                                        conversationChatViewModel.videoCallService.call(chat.friend.friendId)
                                    }
                                    onVideoCallSelected.value = false
                                } else {
                                    //先检查权限
                                    requestVideCallPermission.launchMultiplePermissionRequest()
                                }
                            }

                            else -> {}
                        }
                    }
                )
            }

            composable("location") {
                DragDropSelectPointScreen(onNavigateUp = {
                    navController.navigateUp()
                }, onLocationSend = { latitude: Double, longitude: Double, name: String, address ->
                    conversationChatViewModel.sendChatLocationMessage(
                        latitude,
                        longitude,
                        name,
                        address
                    )
                    navController.navigateUp()
                })
                //AMapScreen(navController)
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
