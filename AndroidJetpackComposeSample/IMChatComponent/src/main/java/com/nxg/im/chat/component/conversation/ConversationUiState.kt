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

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.toMutableStateList
import com.nxg.im.chat.R
import com.nxg.im.core.data.bean.*
import com.nxg.im.core.data.db.entity.Message

class ConversationUiState(
    val channelName: String,
    val channelMembers: Int,
    initialJetChatMessages: List<JetChatMessage>
) {
    private val _Jetchat_messages: MutableList<JetChatMessage> = initialJetChatMessages.toMutableStateList()
    val jetChatMessages: List<JetChatMessage> = _Jetchat_messages

    fun addMessage(msg: JetChatMessage) {
        _Jetchat_messages.add(0, msg) // Add to the beginning of the list
    }
}

@Immutable
data class JetChatMessage(
    val author: String,
    val content: String,
    val timestamp: String,
    val image: Int? = null,
    val authorImage: Int = if (author == "me") R.drawable.ali else R.drawable.someone_else
)

