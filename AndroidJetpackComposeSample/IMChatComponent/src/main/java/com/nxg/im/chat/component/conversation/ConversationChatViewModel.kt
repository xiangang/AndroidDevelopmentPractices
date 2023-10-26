package com.nxg.im.chat.component.conversation

import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.blankj.utilcode.util.Utils
import com.nxg.im.chat.component.notification.NotificationService
import com.nxg.im.core.IMClient
import com.nxg.im.core.data.bean.ChatMessage
import com.nxg.im.core.data.bean.ImageMessage
import com.nxg.im.core.data.bean.ImageMsgContent
import com.nxg.im.core.data.bean.LocationMessage
import com.nxg.im.core.data.bean.LocationMsgContent
import com.nxg.im.core.data.bean.TextMessage
import com.nxg.im.core.data.bean.TextMsgContent
import com.nxg.im.core.data.db.KtChatDatabase
import com.nxg.im.core.data.db.entity.Friend
import com.nxg.im.core.data.db.entity.Message
import com.nxg.im.core.module.upload.UploadService
import com.nxg.im.core.module.user.User
import com.nxg.im.core.module.videocall.VideoCallService
import com.nxg.mvvm.logger.SimpleLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.util.concurrent.atomic.AtomicInteger


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

    var messagePager: Pager<Int, Message>? = null

    private val notificationId = AtomicInteger(1)

    val videoCallService: VideoCallService = IMClient.videoCallService

    /**
     * 创建会话（从通信点击进入聊天的情况需要创建会话）
     */
    fun insertOrReplaceConversation(chatId: Long, chatType: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            IMClient.conversationService.insertOrReplaceConversation(
                chatId,
                chatType
            )
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
                            messagePager = Pager(config = PagingConfig(pageSize = 50)) {
                                KtChatDatabase.getInstance(Utils.getApp()).messageDao()
                                    .pagingSource(
                                        fromId = friend.friendId,
                                        toId = it.user.uuid,
                                        chatType
                                    )
                            }
                        }

                }
            }
        }
    }

    /**
     * 发送聊天文本信息
     */
    fun sendChatTextMessage(text: String) {
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
     * 发送聊天图片信息
     */
    fun sendChatImageMessage(filePath: String) {
        viewModelScope.launch(Dispatchers.IO) {
            IMClient.authService.getLoginData()?.let { loginData ->
                _uiState.value.conversationChat?.let { conversationChat ->
                    let {
                        //发送图片消息
                        try {
                            //获取图片宽高，TODO 压缩图片
                            val option = BitmapFactory.Options()
                            option.inJustDecodeBounds = true
                            BitmapFactory.decodeFile(filePath, option)
                            option.inSampleSize = 1
                            option.inJustDecodeBounds = false
                            val width = option.outWidth
                            val height = option.outHeight
                            logger.debug { "sendChatImageMessage: filePath $filePath, width = $width, height  = $height" }
                            IMClient.chatService.sendMessage(
                                ImageMessage(
                                    loginData.user.uuid,
                                    conversationChat.chatId,
                                    conversationChat.chatType,
                                    ImageMsgContent("", width, height, filePath),
                                    System.currentTimeMillis()
                                )
                            )
                        } catch (e: Exception) {
                            logger.error {
                                e.message
                            }
                        }

                    }
                }
            }
        }
    }

    private val formatter = DecimalFormat("0.000000")

    /**
     * 发送聊天位置信息
     */
    fun sendChatLocationMessage(
        latitude: Double,
        longitude: Double,
        name: String,
        address: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            IMClient.authService.getLoginData()?.let { loginData ->
                _uiState.value.conversationChat?.let { conversationChat ->
                    let {
                        //发送位置消息
                        IMClient.chatService.sendMessage(
                            LocationMessage(
                                loginData.user.uuid,
                                conversationChat.chatId,
                                conversationChat.chatType,
                                LocationMsgContent(
                                    formatter.format(latitude).toDouble(),
                                    formatter.format(longitude).toDouble(),
                                    name,
                                    address
                                ),
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
    fun onMessage(chatMessage: ChatMessage) {
        viewModelScope.launch(Dispatchers.IO) {
            val friend = KtChatDatabase.getInstance(Utils.getApp()).friendDao()
                .getFriend(chatMessage.toId, chatMessage.fromId)
            when (chatMessage) {
                is TextMessage -> {
                    NotificationService.notifyChatMessage(
                        Utils.getApp(),
                        notificationId.getAndIncrement(),
                        friend?.nickname ?: "聊天消息",
                        chatMessage.content.text,
                    )
                }

                else -> {}
            }

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

