package com.nxg.im.core.module.chat

import com.nxg.im.core.IMService
import okio.ByteString

interface ChatService : IMService {

    suspend fun sendMessage(text: String): Boolean

    suspend fun sendMessage(bytes: ByteString): Boolean
}