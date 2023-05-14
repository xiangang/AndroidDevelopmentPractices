package com.nxg.im.core.http

import com.nxg.im.core.IMConstants
import com.nxg.mvvm.logger.SimpleLogger
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener


class SipOKHttp : SimpleLogger {

    private var request: Request = Request.Builder()
        .url("ws://${IMConstants.IM_SERVER_IP}:8080/signaling")
        .addHeader("Authorization", "http://192.168.1.5:1989")
        .build()

    private var webSocket: WebSocket? = null

    private fun init() {
        webSocket = webSocket ?: IMHttpManger.imSignalingOkHttpClient.newWebSocket(
            request,
            object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    super.onOpen(webSocket, response)
                    logger.debug { "onOpen $response" }

                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    super.onMessage(webSocket, text)
                    logger.debug { "onMessage $text" }
                }

                override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                    super.onClosing(webSocket, code, reason)
                    logger.debug { "onClosing $code $reason" }
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    super.onClosed(webSocket, code, reason)
                    logger.debug { "onClosed $code $reason" }
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    super.onFailure(webSocket, t, response)
                    logger.debug { "webSocket ${t.message} $response" }
                }
            })
    }

    fun close() {
        webSocket?.close(1000, "leave room")
        webSocket?.cancel()
    }
}