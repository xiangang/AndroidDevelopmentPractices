package com.nxg.im.core.callback

import com.nxg.im.core.data.bean.ChatMessage

interface OnMessageCallback {

    fun onReceiveMessage(message: ChatMessage)

}