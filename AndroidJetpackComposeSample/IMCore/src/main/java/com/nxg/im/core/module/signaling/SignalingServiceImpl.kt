package com.nxg.im.core.module.signaling

import com.nxg.im.core.IMClient
import com.nxg.im.core.IMConstants
import com.nxg.im.core.IMConstants.Protocol.TYPE_ACK
import com.nxg.im.core.IMConstants.Protocol.TYPE_NOTIFY
import com.nxg.im.core.IMConstants.Protocol.TYPE_REQ
import com.nxg.im.core.IMCoreMessage
import com.nxg.im.core.data.bean.Session
import com.nxg.im.core.dispatcher.IMCoroutineScope
import com.nxg.im.core.http.IMHttpManger
import com.nxg.im.core.http.IMWebSocket
import com.nxg.im.core.module.chat.ChatServiceImpl
import com.nxg.im.core.module.state.VideoCallStateMachine
import com.nxg.mvvm.logger.SimpleLogger
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import okio.ByteString.Companion.toByteString

object SignalingServiceImpl : SignalingService, SimpleLogger {

    /**
     *  发送消息队列
     */
    private val sendSignalingChannel = Channel<Signaling>()

    init {
        handleQueue()
    }

    private fun handleQueue() {
        IMCoroutineScope.launch {
            for (signaling in sendSignalingChannel) {
                doSendSignaling(signaling)
            }
        }
    }

    override fun sendSignaling(signaling: Signaling) {
        logger.debug { "sendSignaling: ${signaling.cmd}-signaling $signaling" }
        IMCoroutineScope.launch {
            sendSignalingChannel.send(signaling)
        }
    }

    private fun doSendSignaling(signaling: Signaling) {
        IMCoroutineScope.launch {
            val body = signaling.toJson().toByteArray()
            val imCoreMessage = IMCoreMessage.newBuilder().apply {
                version = IMConstants.Protocol.Version
                cmd = IMConstants.Protocol.Cmd.Signaling
                subCmd = IMConstants.Protocol.SubCmd.VideoCall
                type = TYPE_REQ
                logId = signaling.id
                seqId = signaling.id
                bodyLen = body.size
                bodyData = com.google.protobuf.ByteString.copyFrom(body)
            }.build()
            //通过WebSocket发送
            val result = IMWebSocket.send(imCoreMessage.toByteArray().toByteString())
            ChatServiceImpl.logger.debug { "doSendSignaling: signaling ${signaling.toJson()} result $result" }
            //这个是发送方的视角
            when (signaling.signalingType) {
                SignalingType.VideoCall -> {
                    when (signaling.cmd) {
                        "invite" -> {
                            VideoCallStateMachine.callOut(Session(signaling.id, signaling))
                        }

                        "cancel" -> {
                            VideoCallStateMachine.cancel(Session(signaling.id, signaling))
                        }

                        "bye" -> {
                            VideoCallStateMachine.disconnect(Session(signaling.id, signaling))
                        }
                    }
                }

                SignalingType.AudioCall -> {

                }
            }
        }
    }

    override fun onReceiveSignaling(imCoreMessage: IMCoreMessage, signaling: Signaling) {
        logger.debug { "onReceiveSignaling: imCoreMessage $imCoreMessage" }
        logger.debug { "onReceiveSignaling: ${signaling.cmd}-signaling $signaling" }
        if (imCoreMessage.cmd != IMConstants.Protocol.Cmd.Signaling) {
            return
        }
        when (imCoreMessage.subCmd) {
            IMConstants.Protocol.SubCmd.VideoCall -> {
                when (signaling.cmd) {
                    "invite" -> {
                        //acknowledge（发送方处理）
                        if (imCoreMessage.type == TYPE_ACK) {
                            //收到服务器回复开始振铃
                            VideoCallStateMachine.ringBack(Session(signaling.id, signaling))
                        }
                        //notify（接收方处理）
                        if (imCoreMessage.type == TYPE_NOTIFY) {
                            //收到服务器消息，呼入
                            VideoCallStateMachine.callIn(Session(signaling.id, signaling))
                            VideoCallStateMachine.ringBack(Session(signaling.id, signaling))
                        }
                    }

                    "cancel" -> {
                        //acknowledge（发送方处理）
                        if (imCoreMessage.type == TYPE_ACK) {
                            VideoCallStateMachine.cancel(Session(signaling.id, signaling))
                        }
                        //notify（接收方处理）
                        if (imCoreMessage.type == TYPE_NOTIFY) {
                            VideoCallStateMachine.hangup(Session(signaling.id, signaling))
                        }
                    }

                    "busy" -> {
                        //notify（接收方处理）
                        if (imCoreMessage.type == TYPE_NOTIFY) {
                            //TODO
                        }
                    }

                    "answer" -> {
                        //acknowledge（发送方处理）
                        if (imCoreMessage.type == TYPE_ACK) {
                            VideoCallStateMachine.connect(Session(signaling.id, signaling))
                        }
                        //notify（接收方处理）
                        if (imCoreMessage.type == TYPE_NOTIFY) {
                            VideoCallStateMachine.answer(Session(signaling.id, signaling))
                            VideoCallStateMachine.connect(Session(signaling.id, signaling))
                        }
                    }

                    "bye" -> {
                        //acknowledge（发送方处理）
                        if (imCoreMessage.type == TYPE_ACK) {
                            VideoCallStateMachine.disconnect(Session(signaling.id, signaling))
                        }
                        //notify（接收方处理）
                        if (imCoreMessage.type == TYPE_NOTIFY) {
                            VideoCallStateMachine.hangup(Session(signaling.id, signaling))
                            VideoCallStateMachine.disconnect(Session(signaling.id, signaling))
                        }
                    }
                }
            }
        }
    }

    override suspend fun generateUid(): Long? {
        try {
            return IMClient.authService.getApiToken()?.let {
                IMHttpManger.imApiService.generateUid(it)
                    .takeIf { apiResult -> apiResult.code == 200 }?.data
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return null
    }
}