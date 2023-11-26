package com.nxg.im.core.module.chat

import com.nxg.im.core.IMService
import com.nxg.im.core.data.db.entity.Message

interface ChatService : IMService {

    fun sendMessage(message: Message)

    fun resendMessage(message: Message)

    fun onReceiveMessage(bytes: ByteArray)

    fun getOfflineMessage(fromId: String)

    suspend fun generateUid(): Long?
}