package com.nxg.im.core.module.chat

import com.nxg.im.core.IMService

interface ChatService : IMService {

    suspend fun sendMessage(text:String)
}