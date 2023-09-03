package com.nxg.im.chat.component.conversation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.blankj.utilcode.util.Utils
import com.nxg.im.core.IMClient
import com.nxg.im.core.data.bean.TextMessage
import com.nxg.im.core.data.bean.TextMsgContent
import com.nxg.im.core.data.db.KtChatDatabase
import com.nxg.im.core.data.db.entity.Conversation
import com.nxg.im.core.data.db.entity.Friend
import com.nxg.im.core.data.db.entity.Message
import com.nxg.im.core.module.user.User
import com.nxg.mvvm.logger.SimpleLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


data class ConversationChat(
    val chatId: Long,
    val chatType: Int,
    val me: User,
    val friend: Friend
)

data class ConversationChatUiState(
    val conversationChat: ConversationChat? = null
)


class ConversationChatViewModel : ViewModel(), SimpleLogger {

    // Backing property to avoid state updates from other classes
    private val _uiState = MutableStateFlow(ConversationChatUiState(null))

    // The UI collects from this StateFlow to get its state updates
    val uiState: StateFlow<ConversationChatUiState> = _uiState.asStateFlow()

    val messagePager = Pager(
        config = PagingConfig(pageSize = 50)
    ) {
        KtChatDatabase.getInstance(Utils.getApp()).messageDao().pagingSource()
    }

    init {
        viewModelScope.launch {

            messagePager.flow.collect {

                logger.debug { "loadConversationChat messagePager.flow update " }

            }
        }
    }

    /**
     * 创建会话（从通信点击进入聊天的情况需要创建会话）
     */
    fun insertOrReplaceConversation(chatId: Long, chatType: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            IMClient.authService.getLoginData()?.let { loginData ->
                KtChatDatabase.getInstance(Utils.getApp().applicationContext).friendDao()
                    .getFriend(loginData.user.uuid, chatId)?.let { friend ->
                        IMClient.conversationService.loadConversations(
                            loginData.user.uuid,
                            chatId,
                            chatType
                        )?.let { conversation ->
                            IMClient.conversationService.updateConversations(
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
    }

    /**
     * 加载会话聊天详情
     */
    fun loadConversationChat(chatId: Long, chatType: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            IMClient.authService.getLoginData()?.let {
                if (chatType == 0) {
                    KtChatDatabase.getInstance(Utils.getApp().applicationContext).friendDao()
                        .getFriend(it.user.uuid, chatId)?.let { friend ->
                            logger.debug { "loadConversationChat: friend $friend" }
                            _uiState.emit(
                                _uiState.value.copy(
                                    conversationChat = ConversationChat(
                                        chatId,
                                        chatType,
                                        it.user,
                                        friend
                                    )
                                )
                            )
                        }
                }
            }
        }
    }

    /**
     * 发送聊天信息
     */
    fun sendMessage(text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            IMClient.authService.getLoginData()?.let { loginData ->
                _uiState.value.conversationChat?.let { conversationChat ->
                    let {
                        //发送文本消息
                        IMClient.chatService.sendMessage(
                            TextMessage(
                                loginData.user.uuid,
                                conversationChat.chatId,
                                conversationChat.chatType,
                                TextMsgContent(text),
                                System.currentTimeMillis()
                            )
                        )
                    }
                }
            }
        }
    }

    /**
     * 重发消息
     */
    fun resendMessage(message: Message) {
        viewModelScope.launch(Dispatchers.IO) {
            IMClient.chatService.resendMessage(message)
        }
    }

    /**
     * 接收聊天信息
     */
    fun onMessage(text: String) {
        viewModelScope.launch(Dispatchers.IO) {

        }
    }

    /**
     * 获取离线消息
     */
    fun getOfflineMessage(fromId: String) {
        logger.debug { "getOfflineMessage: fromId $fromId" }
        viewModelScope.launch(Dispatchers.IO) {
            IMClient.chatService.getOfflineMessage(fromId)
        }
    }

}

