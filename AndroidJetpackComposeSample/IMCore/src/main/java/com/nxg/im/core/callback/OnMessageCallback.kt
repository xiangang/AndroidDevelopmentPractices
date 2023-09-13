package com.nxg.im.core.callback

import com.nxg.im.core.data.bean.IMMessage

interface OnMessageCallback {

    fun onReceiveMessage(message: IMMessage)

}