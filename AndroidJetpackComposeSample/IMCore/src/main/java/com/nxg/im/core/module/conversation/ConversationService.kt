package com.nxg.im.core.module.conversation

import com.nxg.im.core.IMService
import com.nxg.im.core.data.db.entity.Conversation
import kotlinx.coroutines.flow.Flow

interface ConversationService : IMService {

    suspend fun flowConversationList(): Flow<List<Conversation>>

    suspend fun getConversationList(): List<Conversation>

    suspend fun insertConversations(vararg conversations: Conversation)

    suspend fun updateConversations(vararg conversations: Conversation)

    suspend fun deleteConversations(vararg conversations: Conversation)
}