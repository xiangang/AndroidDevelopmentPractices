package com.nxg.im.core.module.chat

import com.nxg.im.core.http.IMWebSocket
import com.nxg.mvvm.logger.SimpleLogger

object ChatServiceImpl : ChatService, SimpleLogger {

    override suspend fun sendMessage(text: String) {
        IMWebSocket.send(text)
    }
}