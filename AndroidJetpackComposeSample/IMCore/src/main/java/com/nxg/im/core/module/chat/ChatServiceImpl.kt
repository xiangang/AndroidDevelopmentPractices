package com.nxg.im.core.module.chat

import android.util.ArrayMap
import com.blankj.utilcode.util.Utils
import com.nxg.im.core.IMClient
import com.nxg.im.core.IMCoreMessage
import com.nxg.im.core.data.bean.*
import com.nxg.im.core.data.db.KtChatDatabase
import com.nxg.im.core.data.db.entity.*
import com.nxg.im.core.dispatcher.IMCoroutineScope
import com.nxg.im.core.http.IMWebSocket
import com.nxg.mvvm.logger.SimpleLogger
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okio.ByteString
import okio.ByteString.Companion.toByteString

object ChatServiceImpl : ChatService, SimpleLogger {

    /**
     *  消息队列
     */
    private val messageChannel = Channel<IMMessage> { }

    /**
     * 发送队列
     */
    private val sendQueue = ArrayMap<Long, Job>()

    /**
     * 确认队列
     */
    private val ackQueue = ArrayMap<Long, Job>()

    init {
        IMCoroutineScope.launch {
            for (imMessage in messageChannel) {
                logger.debug { "imMessage: $imMessage" }
                //处理不同的消息
                when (imMessage) {

                    is TextMessage -> {

                    }

                    else -> {}
                }
                //保存到本地数据库
                val message = Message(
                    0,
                    0,//uuid从服务器的ACK消息获取
                    fromId = imMessage.fromId,
                    toId = imMessage.toId,
                    chatType = 0,
                    msgContent = imMessage.toJson()
                )
                KtChatDatabase.getInstance(Utils.getApp()).messageDao().insertMessage(message).let {
                    message.id = it
                }

                //生成protobuf
                val body = message.msgContent.toByteArray()
                val imCoreMessage = IMCoreMessage.newBuilder().apply {
                    version = 1
                    cmd = "chat"
                    subCmd = "text"
                    type = 0
                    logId = 0
                    seqId = message.id
                    bodyLen = body.size
                    bodyData = com.google.protobuf.ByteString.copyFrom(body)
                }.build()
                //通过WebSocket发送
                var result = IMWebSocket.send(imCoreMessage.toByteArray().toByteString())
                logger.debug { "sendMessage: result $result" }
                //发送成功
                if (result) {
                    //加入ACK队列
                    ackQueue[imCoreMessage.seqId] = IMCoroutineScope.launch {
                        delay(3000)
                        //3秒内无应答代表失败，当然，不一定失败，可能是响应时间过长，因此服务器要支持消息去重
                        if (message.sent != IM_SEND_RESPONSE) {
                            logger.debug { "sendMessage: no ack after 3 seconds" }
                            //超过三次后代表发送失败，改为用户手动触发发送
                            message.sent = IM_SEND_FAILED
                            KtChatDatabase.getInstance(Utils.getApp()).messageDao()
                                .updateMessage(message)
                        }
                        ackQueue.remove(imCoreMessage.seqId)
                    }
                } else {
                    //如果发送失败，尝试发送三遍
                    message.sent = IM_SEND_REQUEST
                    KtChatDatabase.getInstance(Utils.getApp()).messageDao().updateMessage(message)
                    sendQueue[imCoreMessage.seqId] = IMCoroutineScope.launch {
                        //如果在发送中，最多尝试3次
                        while (message.sent == IM_SEND_REQUEST && message.retryCount < IM_RETRY_TIMES) {
                            delay(1500)
                            if (message.retryCount < IM_RETRY_TIMES) {
                                message.retryCount++
                                result =
                                    IMWebSocket.send(imCoreMessage.toByteArray().toByteString())
                                logger.debug { "sendMessage: retry ${message.retryCount} $result" }
                                //如果成功则更新状态为成功
                                if (result) {
                                    message.sent = IM_SEND_DEFAULT
                                    KtChatDatabase.getInstance(Utils.getApp()).messageDao()
                                        .updateMessage(message)
                                    sendQueue[imCoreMessage.seqId]?.cancel()
                                    //发送成功加入ACK队列
                                    ackQueue[imCoreMessage.seqId] = IMCoroutineScope.launch {
                                        delay(3000)
                                        //3秒内无应答代表失败，当然，不一定失败，可能是响应时间过长，因此服务器要支持消息去重
                                        if (message.sent != IM_SEND_RESPONSE) {
                                            logger.debug { "sendMessage: retry ${message.retryCount} but failed" }
                                            //超过三次后代表发送失败，改为用户手动触发发送
                                            message.sent = IM_SEND_FAILED
                                            KtChatDatabase.getInstance(Utils.getApp()).messageDao()
                                                .updateMessage(message)
                                        }
                                        ackQueue.remove(imCoreMessage.seqId)
                                    }
                                    break
                                }
                            }
                        }
                        //重发后都没成功，则认为发送失败
                        if (message.sent != IM_SEND_DEFAULT) {
                            logger.debug { "sendMessage: retry ${message.retryCount} times but failed" }
                            //超过三次后代表发送失败，改为用户手动触发发送
                            message.sent = IM_SEND_FAILED
                            KtChatDatabase.getInstance(Utils.getApp()).messageDao()
                                .updateMessage(message)
                        }
                        sendQueue.remove(imCoreMessage.seqId)
                    }
                }
            }
        }
    }


    override suspend fun sendMessage(text: String): Boolean {
        return IMWebSocket.send(text)
    }

    override suspend fun sendMessage(imMessage: IMMessage) {
        //通过协程Channel实现
        messageChannel.send(imMessage)
    }

    /**
     * 收到服务器消息
     */
    override fun onReceiveMessage(bytes: ByteString) {
        IMCoroutineScope.launch {
            try {
                //处理收到的消息
                val imCoreMessage = IMCoreMessage.parseFrom(bytes.toByteArray())
                logger.debug { "onReceiveMessage imCoreMessage $imCoreMessage" }
                val imMessageJson = imCoreMessage.bodyData.toStringUtf8()
                logger.debug { "onReceiveMessage imMessage $imMessageJson" }
                when (imCoreMessage.cmd) {
                    "chat" -> {
                        when (imCoreMessage.subCmd) {
                            "text" -> {
                                //acknowledge
                                if (imCoreMessage.type == 1) {
                                    logger.debug { "onReceiveMessage cancel ${imCoreMessage.seqId} job and remove!" }
                                    //移除ACK队列中的超时任务
                                    ackQueue[imCoreMessage.seqId]?.cancel()
                                    ackQueue.remove(imCoreMessage.seqId)
                                }
                                //notify
                                if (imCoreMessage.type == 2) {
                                    IMClient.onMessageCallback?.onMessage(imMessageJson)
                                }
                            }
                        }
                    }
                }

            } catch (e: Exception) {
                println("onReceiveMessage ${e.message}")
            }
        }
    }
}