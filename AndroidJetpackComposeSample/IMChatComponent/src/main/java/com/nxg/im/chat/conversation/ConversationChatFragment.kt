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

package com.nxg.im.chat.conversation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.nxg.im.chat.MainViewModel
import com.nxg.im.chat.data.exampleUiState
import com.nxg.im.commonui.theme.JetchatTheme
import com.nxg.im.chat.R
import com.nxg.mvvm.logger.SimpleLogger
import com.nxg.mvvm.ui.BaseBusinessFragment
import kotlinx.coroutines.launch

class ConversationChatFragment : BaseBusinessFragment(), SimpleLogger {

    private val activityViewModel: MainViewModel by activityViewModels()

    private val conversationChatViewModel: ConversationChatViewModel by activityViewModels()

    private val safeArgs: ConversationChatFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(inflater.context).apply {

        setContent {
            JetchatTheme {
                ConversationContent(
                    conversationChatViewModel,
                    uiState = exampleUiState,
                    navigateToProfile = { user ->
                        // Click callback
                        val bundle = bundleOf("userId" to user)
                        findNavController().navigate(
                            R.id.nav_profile,
                            bundle
                        )
                    },
                    onNavIconPressed = {
                        activityViewModel.openDrawer()
                    },
                    onMessageSent = {
                        conversationChatViewModel.sendMessage(it)
                    },
                    onNavigateUp = {
                        findNavController().navigateUp()
                    }
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logger.debug { "chatId: ${safeArgs.chatId}" }
        logger.debug { "chatType: ${safeArgs.chatType}" }
        conversationChatViewModel.insertOrReplaceConversations(safeArgs.chatId, safeArgs.chatType)
        conversationChatViewModel.loadConversationChat(safeArgs.chatId, safeArgs.chatType)
        lifecycleScope.launch {
            conversationChatViewModel.uiState.collect {
                logger.debug { "uiState: ${it.conversationChat?.messages}" }
            }
        }
    }

}
