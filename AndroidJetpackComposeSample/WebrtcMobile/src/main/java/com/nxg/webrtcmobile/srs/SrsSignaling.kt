package com.nxg.webrtcmobile.srs

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.nxg.mvvm.logger.SimpleLogger
import io.socket.engineio.client.Socket
import io.socket.engineio.client.Socket.EVENT_OPEN
import kotlinx.coroutines.*
import java.util.concurrent.Executors

//SrsSignaling调度器
val SrsSignalingDispatcher: CoroutineDispatcher = Executors.newSingleThreadExecutor { runnable ->
    Thread(runnable, "SdkDataBaseDispatcher")
}.asCoroutineDispatcher()


//SrsSignaling作用域
val SrsSignalingScope: CoroutineScope = CoroutineScope(SrsSignalingDispatcher + SupervisorJob())

class SrsSignalingObserver : DefaultLifecycleObserver, SimpleLogger {
    private val roomName = "live"
    private val displayName: String by lazy {
        "${System.currentTimeMillis()}"
    }
    private var job: Job? = null
    private var socket: Socket? = null

    private fun join() {
        socket?.send("{\"tid\":\"${System.currentTimeMillis()}\",\"msg\":{\"action\":\"join\",\"room\":\"$roomName\",\"display\":\"$displayName\"}}")
    }

    private fun publish() {
        socket?.send("{\"tid\":\"${System.currentTimeMillis()}\",\"msg\":{\"action\":\"publish\",\"room\":\"$roomName\",\"display\":\"$displayName\"}}")
    }

    override fun onCreate(owner: LifecycleOwner) {
        logger.debug { "onCreate" }
        socket = socket
            ?: Socket("ws://192.168.1.5:1989/sig/v1/rtc?room=$roomName&display=$displayName").apply {
                on("join") {
                    logger.debug { "on join $it" }
                }
                on("publish") {
                    logger.debug { "on publish $it" }
                }
                on("control") {
                    logger.debug { "on control $it " }
                }
                on("leave") {
                    logger.debug { "on leave $it " }
                }
            }
    }

    override fun onResume(owner: LifecycleOwner) {
        logger.debug { "onResume" }
        socket?.open()
    }

    override fun onPause(owner: LifecycleOwner) {
        logger.debug { "onPause" }
        socket?.close()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        job?.cancel()
    }
}