package com.nxg.im.core.module.chat

import com.nxg.im.core.IMService
import com.nxg.im.core.data.bean.IMMessage
import com.nxg.im.core.data.db.entity.Message
import okio.ByteString

interface ChatService : IMService {

    suspend fun sendMessage(text: String): Boolean

    suspend fun sendMessage(imMessage: IMMessage)

    fun onReceiveMessage(bytes: ByteString)
}