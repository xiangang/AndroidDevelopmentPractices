package com.nxg.im.chat.conversation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.Utils
import com.nxg.im.core.IMClient
import com.nxg.im.core.data.bean.TextMessage
import com.nxg.im.core.data.bean.TextMsgContent
import com.nxg.im.core.data.db.KtChatDatabase
import com.nxg.im.core.data.db.entity.Conversation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.nxg.im.core.data.db.entity.Message
import com.nxg.im.core.data.bean.toJson
import com.nxg.im.core.data.db.entity.Friend


data class ConversationChat(val chatId: Long, val chatType: Int, val friend: Friend, val messages: List<Message>)

data class ConversationChatUiState(
    val conversationChat: ConversationChat? = null
)

class ConversationChatViewModel : ViewModel() {

    // Backing property to avoid state updates from other classes
    private val _uiState = MutableStateFlow(ConversationChatUiState(null))

    // The UI collects from this StateFlow to get its state updates
    val uiState: StateFlow<ConversationChatUiState> = _uiState.asStateFlow()

    fun insertOrReplaceConversations(chatId: Long, chatType: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            IMClient.authService.getLoginData()?.let { loginData ->
                val friend =
                    KtChatDatabase.getInstance(Utils.getApp().applicationContext).friendDao()
                        .getFriend(chatId)
                IMClient.conversationService.loadConversations(
                    loginData.user.uuid,
                    chatId,
                    chatType
                )
                    ?.let { conversation ->
                        IMClient.conversationService.insertConversations(
                            conversation.copy(
                                userId = loginData.user.uuid,
                                chatId = chatId,
                                chatType = chatType,
                                name = friend.nickname,
                                coverImage = friend.avatar,
                                backgroundImage = "",
                                lastMsgId = 0,
                                lastMsgContent = "",
                                updateTime = System.currentTimeMillis(),
                                unreadCount = 0,
                                draft = "",
                                top = false,
                                sticky = false,
                                remind = false,
                            )
                        )
                    } ?: let {
                    val conversation = Conversation(
                        userId = loginData.user.uuid,
                        chatId = chatId,
                        chatType = chatType,
                        name = friend.nickname,
                        coverImage = friend.avatar,
                        backgroundImage = "",
                        lastMsgId = 0,
                        lastMsgContent = "",
                        createTime = System.currentTimeMillis(),
                        updateTime = System.currentTimeMillis(),
                        unreadCount = 0,
                        draft = "",
                        top = false,
                        sticky = false,
                        remind = false,
                    )
                    IMClient.conversationService.insertConversations(conversation)
                }

            }
        }
    }

    fun loadConversationChat(chatId: Long, chatType: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            IMClient.authService.getLoginData()?.let {
                if (chatType == 0) {
                    val friend =
                        KtChatDatabase.getInstance(Utils.getApp().applicationContext).friendDao()
                            .getFriend(chatId)
                    val messages =
                        KtChatDatabase.getInstance(Utils.getApp().applicationContext).messageDao()
                            .loadMessages(chatId, chatId, chatType)
                    _uiState.emit(
                        _uiState.value.copy(
                            conversationChat = ConversationChat(
                                chatId,
                                chatType,
                                friend,
                                messages
                            )
                        )
                    )
                }
            }
        }
    }

    fun sendMessage(text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            IMClient.authService.getLoginData()?.let { loginData ->
                _uiState.value.conversationChat?.let { conversationChat ->
                    let {
                        IMClient.chatService.sendMessage(
                            TextMessage(
                                loginData.user.uuid,
                                conversationChat.chatId,
                                conversationChat.chatType,
                                TextMsgContent(text),
                                System.currentTimeMillis()
                            ).toJson()
                        )
                    }
                }
            }
        }
    }
}

