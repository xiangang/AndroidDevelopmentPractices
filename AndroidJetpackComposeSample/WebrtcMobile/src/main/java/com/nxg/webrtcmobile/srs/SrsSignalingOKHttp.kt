package com.nxg.webrtcmobile.srs

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.GsonUtils
import com.nxg.mvvm.logger.SimpleLogger
import kotlinx.coroutines.*
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

/**
 * 信令
 */
interface ISignalingCallBack {

    /**
     * 推流
     */
    fun onPublish(streamUrl: String)

    /**
     * 推流
     */
    fun onPlay(streamUrl: String)
}


class SrsSignalingOkHttpObserver : DefaultLifecycleObserver, SimpleLogger {
    private val roomName = "live"
    private val displayName: String by lazy {
        "${System.currentTimeMillis()}"
    }

    private var request: Request = Request.Builder()
        .url("ws://192.168.1.5:1989/sig/v1/rtc?room=$roomName&display=$displayName")
        .addHeader("Origin", "http://192.168.1.5:1989")
        .build()

    private var webSocket: WebSocket? = null

    var signalingCallBack: ISignalingCallBack? = null


    private fun join() {
        webSocket?.send("{\"tid\":\"${System.currentTimeMillis()}\",\"msg\":{\"action\":\"join\",\"room\":\"$roomName\",\"display\":\"$displayName\"}}")
    }

    private fun publish() {
        webSocket?.send("{\"tid\":\"${System.currentTimeMillis()}\",\"msg\":{\"action\":\"publish\",\"room\":\"$roomName\",\"display\":\"$displayName\"}}")
    }

    override fun onCreate(owner: LifecycleOwner) {
        logger.debug { "onCreate" }
        webSocket = webSocket ?: SrsHttpManger.srsSignalingOkHttpClient.newWebSocket(
            request,
            object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    super.onOpen(webSocket, response)
                    logger.debug { "onOpen $response" }
                    join()
                    signalingCallBack?.onPublish("webrtc://${SrsConstant.SRS_SERVER_IP}/$roomName/$displayName")
                    publish()
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    super.onMessage(webSocket, text)
                    logger.debug { "onMessage $text" }
                    val srsSignalingBean =
                        GsonUtils.getGson().fromJson(text, SrsSignalingBean::class.java)
                    srsSignalingBean.msg?.peer?.publishing?.takeIf { it }?.let {
                        signalingCallBack?.onPlay("webrtc://${SrsConstant.SRS_SERVER_IP}/$roomName/${srsSignalingBean.msg?.peer?.display}")
                    }
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

    override fun onResume(owner: LifecycleOwner) {
        logger.debug { "onResume" }
    }

    override fun onPause(owner: LifecycleOwner) {
        logger.debug { "onPause" }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        webSocket?.close(1000, "leave room")
        webSocket?.cancel()
    }
}