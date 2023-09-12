package com.nxg.im.core.module.conversation

import com.blankj.utilcode.util.Utils
import com.nxg.im.core.IMClient
import com.nxg.im.core.data.db.KtChatDatabase
import com.nxg.im.core.data.db.dao.ConversationDao
import com.nxg.im.core.data.db.entity.Conversation
import com.nxg.im.core.module.auth.AuthServiceImpl
import com.nxg.mvvm.logger.SimpleLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

object ConversationServiceImpl : ConversationService, SimpleLogger {

    private val conversationDao: ConversationDao =
        KtChatDatabase.getInstance(Utils.getApp()).conversationDao()

    override suspend fun flowConversationList(): Flow<List<Conversation>> {
        return IMClient.authService.getLoginData()?.let {
            conversationDao.flowConversationList(it.user.uuid)
        } ?: MutableStateFlow(emptyList())
    }

    override suspend fun getConversationList(): List<Conversation> {
        return IMClient.authService.getLoginData()?.let {
            conversationDao.getConversations(it.user.uuid)
        } ?: emptyList()
    }

    override suspend fun insertConversations(vararg conversations: Conversation) {
        conversationDao.insertConversations(*conversations)
    }

    override suspend fun loadConversations(
        userId: Long,
        chatId: Long,
        chatType: Int
    ): Conversation? {
        return conversationDao.loadConversations(userId, chatId, chatType)
    }

    override suspend fun updateConversations(vararg conversations: Conversation) {
        conversationDao.updateConversations(*conversations)
    }

    override suspend fun deleteConversations(vararg conversations: Conversation) {
        conversationDao.deleteConversations(*conversations)
    }

    /**
     * @param chatId 如果是单聊，则是发送消息的用户的id
     * @param chatType 0单聊，1群聊
     */
    override suspend fun insertOrReplaceConversation(chatId: Long, chatType: Int) {
        AuthServiceImpl.getLoginData()?.let { loginData ->
            KtChatDatabase.getInstance(Utils.getApp().applicationContext)
                .friendDao()
                .getFriend(loginData.user.uuid, chatId)
                ?.let { friend ->
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
                        IMClient.conversationService.insertConversations(
                            conversation
                        )
                    }
                }
        }
    }
}