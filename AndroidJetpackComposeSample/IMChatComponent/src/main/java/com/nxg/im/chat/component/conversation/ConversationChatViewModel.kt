package com.nxg.im.chat.component.conversation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.blankj.utilcode.util.Utils
import com.google.protobuf.ByteString
import com.nxg.im.core.IMClient
import com.nxg.im.core.IMCoreMessage
import com.nxg.im.core.data.bean.*
import com.nxg.im.core.data.db.KtChatDatabase
import com.nxg.im.core.data.db.entity.Conversation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.nxg.im.core.data.db.entity.Message
import com.nxg.im.core.data.db.entity.Friend
import com.nxg.im.core.module.user.User
import com.nxg.mvvm.logger.SimpleLogger
import okio.ByteString.Companion.toByteString
import java.io.ByteArrayOutputStream


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

    /**
     * 创建会话
     */
    fun insertOrReplaceConversation(chatId: Long, chatType: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            IMClient.authService.getLoginData()?.let { loginData ->
                val friend =
                    KtChatDatabase.getInstance(Utils.getApp().applicationContext).friendDao()
                        .getFriend(chatId)
                IMClient.conversationService.loadConversations(
                    loginData.user.uuid,
                    chatId,
                    chatType
                )?.let { conversation ->
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

    /**
     * 加载会话聊天详情
     */
    fun loadConversationChat(chatId: Long, chatType: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            IMClient.authService.getLoginData()?.let {
                if (chatType == 0) {
                    val friend =
                        KtChatDatabase.getInstance(Utils.getApp().applicationContext).friendDao()
                            .getFriend(chatId)
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

    /**
     * 发送聊天信息
     */
    fun sendMessage(text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            IMClient.authService.getLoginData()?.let { loginData ->
                _uiState.value.conversationChat?.let { conversationChat ->
                    let {
                        //保存到本地数据库
                        val msgContent = TextMessage(
                            loginData.user.uuid,
                            conversationChat.chatId,
                            conversationChat.chatType,
                            TextMsgContent(text),
                            System.currentTimeMillis()
                        ).toJson()
                        val message = Message(
                            0,
                            0,
                            fromId = loginData.user.uuid,
                            toId = conversationChat.chatId,
                            chatType = 0,
                            msgContent = msgContent
                        )
                        KtChatDatabase.getInstance(Utils.getApp()).messageDao()
                            .insertMessages(message)
                        //通过WebSocket发送
                        val body = msgContent.toByteArray()
                        val imCoreMessage = IMCoreMessage.newBuilder().apply {
                            version = 1
                            cmd = "chat"
                            subCmd = "text"
                            type = 0
                            logId = 0
                            seqId = 0
                            bodyLen = body.size
                            bodyData = ByteString.copyFrom(body)
                        }.build()
                        val byteString = imCoreMessage.toByteArray().toByteString()
                        IMClient.chatService.sendMessage(byteString)
                    }
                }
            }
        }
    }

    /**
     * 接收聊天信息
     */
    fun onMessage(text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val imMessage = text.parseIMMessage()
            val message = Message(
                0,
                0,
                fromId = imMessage.fromId,
                toId = imMessage.toId,
                chatType = imMessage.chatType,
                msgContent = text
            )
            KtChatDatabase.getInstance(Utils.getApp()).messageDao()
                .insertMessages(message)
            //单聊，如果没有会话，则创建
            if (imMessage.chatType == 0) {
                val friend =
                    KtChatDatabase.getInstance(Utils.getApp().applicationContext).friendDao()
                        .getFriend(imMessage.fromId)
                IMClient.conversationService.loadConversations(
                    imMessage.toId,
                    imMessage.fromId,
                    imMessage.chatType
                )?.let { conversation ->
                    IMClient.conversationService.insertConversations(
                        conversation.copy(
                            imMessage.toId,
                            imMessage.fromId,
                            chatType = imMessage.chatType,
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
                        imMessage.toId,
                        imMessage.fromId,
                        chatType = imMessage.chatType,
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

