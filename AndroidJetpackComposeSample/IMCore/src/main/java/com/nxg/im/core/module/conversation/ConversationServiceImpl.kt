package com.nxg.im.core.module.conversation

import com.blankj.utilcode.util.Utils
import com.nxg.im.core.IMClient
import com.nxg.im.core.data.db.KtChatDatabase
import com.nxg.im.core.data.db.dao.ConversationDao
import com.nxg.im.core.data.db.entity.Conversation
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

    override suspend fun updateConversations(vararg conversations: Conversation) {
        conversationDao.updateConversations(*conversations)
    }

    override suspend fun deleteConversations(vararg conversations: Conversation) {
        conversationDao.deleteConversations(*conversations)
    }
}