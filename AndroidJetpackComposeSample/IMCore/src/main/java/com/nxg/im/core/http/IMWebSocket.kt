package com.nxg.im.core.http

import com.nxg.im.core.IMClient
import com.nxg.im.core.IMConstants
import com.nxg.im.core.IMCoreMessage
import com.nxg.im.core.dispatcher.IMCoroutineScope
import com.nxg.mvvm.logger.SimpleLogger
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.util.concurrent.atomic.AtomicBoolean

/**
 * WebSocket
 */
object IMWebSocket : SimpleLogger {

    private var webSocket: WebSocket? = null

    private val init = AtomicBoolean(false)

    @Volatile
    private var connected: Boolean = false


    fun init() {
        logger.debug { "init" }
        if (init.getAndSet(true))
            return
        IMCoroutineScope.launch {
            while (true) {
                if (!connected) {
                    IMClient.authService.getWebSocketToken()?.let {
                        webSocket = IMHttpManger.imSignalingOkHttpClient.newWebSocket(
                            request = Request.Builder()
                                .url("ws://${IMConstants.IM_SERVER_IP}:${IMConstants.IM_SERVER_HTTP_PORT}/chat")
                                .addHeader("Authorization", it)
                                .build(),
                            object : WebSocketListener() {
                                override fun onOpen(webSocket: WebSocket, response: Response) {
                                    super.onOpen(webSocket, response)
                                    logger.debug { "onOpen $response" }
                                    connected = true
                                }

                                override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                                    super.onMessage(webSocket, bytes)
                                    //处理收到的消息
                                    IMClient.chatService.onReceiveMessage(bytes)
                                }

                                override fun onClosing(
                                    webSocket: WebSocket,
                                    code: Int,
                                    reason: String
                                ) {
                                    super.onClosing(webSocket, code, reason)
                                    logger.debug { "onClosing $code $reason" }
                                }

                                override fun onClosed(
                                    webSocket: WebSocket,
                                    code: Int,
                                    reason: String
                                ) {
                                    super.onClosed(webSocket, code, reason)
                                    logger.debug { "onClosed $code $reason" }
                                    connected = false
                                }

                                override fun onFailure(
                                    webSocket: WebSocket,
                                    t: Throwable,
                                    response: Response?
                                ) {
                                    super.onFailure(webSocket, t, response)
                                    connected = false
                                    logger.debug { "onFailure ${t.message} $response" }
                                }
                            })
                    }

                }
                delay(1000)
            }
        }
    }

    fun send(text: String): Boolean {
        logger.debug { "sendMessage $text" }
        return webSocket?.send(text) ?: false
    }

    fun send(bytes: ByteString): Boolean {
        logger.debug { "sendMessage $bytes" }
        return webSocket?.send(bytes) ?: false
    }

    fun close() {
        webSocket?.close(1000, "leave room")
        webSocket?.cancel()
    }
}