package com.nxg.im.core.module.chat

import com.nxg.im.core.http.IMWebSocket
import com.nxg.mvvm.logger.SimpleLogger
import okio.ByteString

object ChatServiceImpl : ChatService, SimpleLogger {

    override suspend fun sendMessage(text: String): Boolean {
        return IMWebSocket.send(text)
    }

    override suspend fun sendMessage(bytes: ByteString): Boolean {
        return IMWebSocket.send(bytes)
    }
}