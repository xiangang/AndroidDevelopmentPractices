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
     *  发送消息队列
     */
    private val sendMessageChannel = Channel<IMMessage> { }

    /**
     *  接收消息队列
     */
    private val receiveMessageChannel = Channel<ByteString> { }

    /**
     * 重发Map
     */
    private val resendMap = ArrayMap<Long, Job>()

    /**
     * 确认Map
     */
    private val ackMap = ArrayMap<Long, Job>()

    init {

        handleSendMessageQueue()

        handleReceiveMessage()
    }

    override fun sendMessage(imMessage: IMMessage) {
        IMCoroutineScope.launch {
            sendMessageChannel.send(imMessage)
        }
    }

    private suspend fun doSendMessage(message: Message) {
        logger.debug { "message: $message" }
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
        logger.debug { "doSendMessage: result $result" }
        //发送成功
        if (result) {
            message.sent = IM_SEND_DEFAULT
            KtChatDatabase.getInstance(Utils.getApp()).messageDao().updateMessage(message)
            //加入ACK队列
            ackMap[imCoreMessage.seqId] = IMCoroutineScope.launch {
                delay(3000)
                //3秒内无应答代表失败，当然，不一定失败，可能是响应时间过长，因此服务器要支持消息去重
                if (message.sent != IM_SEND_RESPONSE) {
                    logger.debug { "doSendMessage: no ack after 3 seconds" }
                    //超过三次后代表发送失败，改为用户手动触发发送
                    message.sent = IM_SEND_FAILED
                    KtChatDatabase.getInstance(Utils.getApp()).messageDao().updateMessage(message)
                }
                ackMap.remove(imCoreMessage.seqId)
            }
        } else {
            //如果发送失败，尝试发送三遍
            message.sent = IM_SEND_REQUEST
            KtChatDatabase.getInstance(Utils.getApp()).messageDao().updateMessage(message)
            resendMap[imCoreMessage.seqId] = IMCoroutineScope.launch {
                //如果在发送中，最多尝试3次
                while (message.sent == IM_SEND_REQUEST && message.retryCount < IM_RETRY_TIMES) {
                    delay(1500)
                    if (message.retryCount < IM_RETRY_TIMES) {
                        message.retryCount++
                        result = IMWebSocket.send(imCoreMessage.toByteArray().toByteString())
                        logger.debug { "doSendMessage: retry ${message.retryCount} $result" }
                        //如果成功则更新状态为成功
                        if (result) {
                            message.sent = IM_SEND_DEFAULT
                            KtChatDatabase.getInstance(Utils.getApp()).messageDao()
                                .updateMessage(message)
                            resendMap[imCoreMessage.seqId]?.cancel()
                            //发送成功加入ACK队列
                            ackMap[imCoreMessage.seqId] = IMCoroutineScope.launch {
                                delay(3000)
                                //3秒内无应答代表失败，当然，不一定失败，可能是响应时间过长，因此服务器要支持消息去重
                                if (message.sent != IM_SEND_RESPONSE) {
                                    logger.debug { "doSendMessage: retry ${message.retryCount} but failed" }
                                    //超过三次后代表发送失败，改为用户手动触发发送
                                    message.sent = IM_SEND_FAILED
                                    KtChatDatabase.getInstance(Utils.getApp()).messageDao()
                                        .updateMessage(message)
                                }
                                ackMap.remove(imCoreMessage.seqId)
                            }
                            break
                        }
                    }
                }
                //重发后都没成功，则认为发送失败
                if (message.sent != IM_SEND_DEFAULT) {
                    logger.debug { "doSendMessage: retry ${message.retryCount} times but failed" }
                    //超过三次后代表发送失败，改为用户手动触发发送
                    message.sent = IM_SEND_FAILED
                    KtChatDatabase.getInstance(Utils.getApp()).messageDao()
                        .updateMessage(message)
                }
                resendMap.remove(imCoreMessage.seqId)
            }
        }
    }

    private fun handleSendMessageQueue() {
        IMCoroutineScope.launch {
            for (imMessage in sendMessageChannel) {
                logger.debug { "imMessage: $imMessage" }
                //处理不同的消息
                when (imMessage) {
                    is AudioMessage -> {}
                    is FileMessage -> {}
                    is ImageMessage -> {}
                    is LocationMessage -> {}
                    is TextMessage -> {}
                    is VideoMessage -> {}
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
                message.id =
                    KtChatDatabase.getInstance(Utils.getApp()).messageDao().insertMessage(message)
                doSendMessage(message)
            }
        }
    }


    override fun resendMessage(message: Message) {
        IMCoroutineScope.launch {
            doSendMessage(message)
        }
    }

    override fun onReceiveMessage(bytes: ByteString) {
        IMCoroutineScope.launch {
            receiveMessageChannel.send(bytes)
        }
    }

    private fun handleReceiveMessage() {
        IMCoroutineScope.launch {
            for (bytes in receiveMessageChannel) {
                logger.debug { "bytes: $bytes" }
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
                                        ackMap[imCoreMessage.seqId]?.cancel()
                                        ackMap.remove(imCoreMessage.seqId)
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
}