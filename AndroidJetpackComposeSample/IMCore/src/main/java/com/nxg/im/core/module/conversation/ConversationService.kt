package com.nxg.im.core.module.conversation

import com.nxg.im.core.IMService
import com.nxg.im.core.data.db.entity.Conversation
import kotlinx.coroutines.flow.Flow

interface ConversationService : IMService {

    suspend fun flowConversationList(): Flow<List<Conversation>>

    suspend fun getConversationList(): List<Conversation>

    suspend fun loadConversations(userId: Long, chatId: Long, chatType: Int): Conversation?

    suspend fun insertConversations(vararg conversations: Conversation)

    suspend fun updateConversations(vararg conversations: Conversation)

    suspend fun deleteConversations(vararg conversations: Conversation)

    /**
     * @param chatId 如果是单聊，则是发送消息的用户的id
     * @param chatType 0单聊，1群聊
     */
    suspend fun insertOrReplaceConversation(chatId: Long, chatType: Int)


}