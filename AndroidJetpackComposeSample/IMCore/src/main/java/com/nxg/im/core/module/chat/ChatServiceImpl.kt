package com.nxg.im.core.module.chat

import android.graphics.Bitmap
import android.net.Uri
import android.util.ArrayMap
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import com.blankj.utilcode.util.Utils
import com.nxg.im.core.IMClient
import com.nxg.im.core.IMConstants
import com.nxg.im.core.IMCoreMessage
import com.nxg.im.core.data.bean.*
import com.nxg.im.core.data.db.KtChatDatabase
import com.nxg.im.core.data.db.entity.*
import com.nxg.im.core.dispatcher.IMCoroutineScope
import com.nxg.im.core.exception.IMException
import com.nxg.im.core.http.IMHttpManger
import com.nxg.im.core.http.IMWebSocket
import com.nxg.im.core.module.signaling.SignalingServiceImpl
import com.nxg.im.core.module.signaling.parseSignaling
import com.nxg.im.core.module.upload.UploadService
import com.nxg.im.core.utils.VideoUtils
import com.nxg.mvvm.logger.SimpleLogger
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okio.ByteString.Companion.toByteString
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.charset.StandardCharsets

object ChatServiceImpl : ChatService, SimpleLogger {

    /**
     *  发送消息队列
     */
    private val sendMessageChannel = Channel<Message> { }

    /**
     *  接收消息队列
     */
    private val receiveMessageChannel = Channel<ByteArray> { }

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


    override fun sendMessage(message: Message) {
        logger.debug { "sendMessage: $message" }
        IMCoroutineScope.launch {
            sendMessageChannel.send(message)
        }
    }

    /**
     * 真正通过WebSocket发送消息的方法
     */
    private suspend fun doSendMessage(message: Message) {
        logger.debug { "doSendMessage: $message" }
        //处理不同的消息
        when (val chatMessage = message.toChatMessage()) {
            is AudioMessage -> {}
            is FileMessage -> {}
            is ImageMessage -> {
                val messageContent = chatMessage.content
                if (messageContent.url.isEmpty()) {
                    val uploadFilePath = message.uploadFilePath
                    //先上传图片，同时回调上传进度
                    var uploadFileUrl: String? = ""
                    try {
                        uploadFileUrl =
                            IMClient.getService<UploadService>()
                                .syncUpload(uploadFilePath)
                        logger.debug { "doSendMessage: uploadFileUrl $uploadFileUrl" }
                    } catch (e: Exception) {
                        logger.error {
                            e.message
                        }
                    }
                    //更新url
                    if (uploadFileUrl.isNullOrEmpty()) {
                        //处理上传失败的情况
                        message.sent = IM_SEND_FAILED
                        KtChatDatabase.getInstance(Utils.getApp()).messageDao()
                            .updateMessage(message)
                        return
                    }
                    chatMessage.content.url = uploadFileUrl
                    message.msgContent = chatMessage.toJson()
                    KtChatDatabase.getInstance(Utils.getApp()).messageDao()
                        .updateMessage(message)
                }
            }

            is VideoMessage -> {
                val messageContent = chatMessage.content
                if (messageContent.url.isEmpty()) {
                    val uploadFilePath = message.uploadFilePath
                    logger.debug { "doSendMessage: uploadFilePath $uploadFilePath" }
                    //先上传视频封面
                    var uploadVideoThumbnailFileUrl: String? = ""
                    val file = File(uploadFilePath)
                    logger.debug { "doSendMessage: file ${file.absolutePath}" }
                    val videoUri: Uri = FileProvider.getUriForFile(
                        Utils.getApp(),
                        "com.nxg.androidsample.provider",
                        file
                    )
                    logger.debug { "doSendMessage: videoUri $videoUri" }
                    try {
                        VideoUtils.videoFrameDecoder.decode(uploadFilePath).drawable.toBitmap()
                            .let { videoThumbnail ->
                                logger.debug { "doSendMessage: videoThumbnail ${videoThumbnail.width} x ${videoThumbnail.height}" }
                                val stream = ByteArrayOutputStream()
                                videoThumbnail.compress(Bitmap.CompressFormat.PNG, 100, stream)
                                val byteArray = stream.toByteArray()
                                videoThumbnail.recycle()
                                uploadVideoThumbnailFileUrl =
                                    IMClient.getService<UploadService>()
                                        .syncUpload(
                                            byteArray,
                                            file.name.replace(".mp4", "_thumbnail.png")
                                        )
                                logger.debug { "doSendMessage: uploadVideoThumbnailFileUrl $uploadVideoThumbnailFileUrl" }
                            }
                    } catch (e: Exception) {
                        logger.error {
                            e.message
                        }
                    }
                    //先上传视频，同时回调上传进度
                    var uploadFileUrl: String? = ""
                    try {
                        uploadFileUrl =
                            IMClient.getService<UploadService>()
                                .syncUpload(uploadFilePath)
                        logger.debug { "doSendMessage: uploadFileUrl $uploadFileUrl" }
                    } catch (e: Exception) {
                        logger.error {
                            e.message
                        }
                    }
                    //更新url
                    if (uploadFileUrl.isNullOrEmpty()) {
                        //处理上传失败的情况
                        message.sent = IM_SEND_FAILED
                        KtChatDatabase.getInstance(Utils.getApp()).messageDao()
                            .updateMessage(message)
                        return
                    }
                    chatMessage.content.url = uploadFileUrl
                    chatMessage.content.thumbnailUrl = uploadVideoThumbnailFileUrl ?: ""
                    message.msgContent = chatMessage.toJson()
                    KtChatDatabase.getInstance(Utils.getApp()).messageDao()
                        .updateMessage(message)
                }
            }

            is LocationMessage -> {}
            is TextMessage -> {}
        }

        if (message.uuid == 0L) {
            //获取uuid
            val uuid = generateUid()
            logger.debug { "doSendMessage: uuid $uuid" }
            if (uuid == null) {
                retryWhenSendFailed(message)
                return
            }
            message.uuid = uuid
            KtChatDatabase.getInstance(Utils.getApp()).messageDao()
                .updateMessage(message)
        }
        logger.debug { "doSendMessage: message $message" }
        logger.debug { "doSendMessage: message.msgContent ${message.msgContent}" }
        //生成protobuf
        val body = message.msgContent.toByteArray()
        val imCoreMessage = IMCoreMessage.newBuilder().apply {
            version = 1
            cmd = "chat"
            subCmd = "text"
            type = 0
            logId = message.id
            seqId = message.uuid
            bodyLen = body.size
            bodyData = com.google.protobuf.ByteString.copyFrom(body)
        }.build()
        //通过WebSocket发送
        val result = IMWebSocket.send(imCoreMessage.toByteArray().toByteString())
        logger.debug { "doSendMessage: imCoreMessage $imCoreMessage" }
        logger.debug { "doSendMessage: result $result" }
        //发送成功
        if (!result) {
            retryWhenSendFailed(message)
            return
        }
        message.sent = IM_SEND_DEFAULT
        KtChatDatabase.getInstance(Utils.getApp()).messageDao().updateMessage(message)
        //加入ACK队列
        ackMap[imCoreMessage.seqId] = IMCoroutineScope.launch {
            logger.debug { "doSendMessage: ${imCoreMessage.seqId} " }
            delay(3000)
            logger.debug { "doSendMessage: ${imCoreMessage.seqId} " + isActive }
            //3秒内无应答代表失败，当然，不一定失败，可能是响应时间过长，因此服务器要支持消息去重
            if (message.sent != IM_SEND_RESPONSE) {
                logger.debug { "doSendMessage: no ack after 3 seconds" }
                //超过三次后代表发送失败，改为用户手动触发发送
                message.sent = IM_SEND_FAILED
                KtChatDatabase.getInstance(Utils.getApp()).messageDao().updateMessage(message)
            }
            ackMap.remove(imCoreMessage.seqId)
        }
    }

    private suspend fun retryWhenSendFailed(
        message: Message,
        imCoreMessageRetry: IMCoreMessage? = null
    ) {
        val imCoreMessage = imCoreMessageRetry ?: IMCoreMessage.newBuilder().apply {
            //生成protobuf
            val body = message.msgContent.toByteArray()
            version = IMConstants.Protocol.Version
            cmd = IMConstants.Protocol.Cmd.Chat
            subCmd = IMConstants.Protocol.SubCmd.Text
            type = IMConstants.Protocol.TYPE_REQ
            logId = message.id
            seqId = message.uuid
            bodyLen = body.size
            bodyData = com.google.protobuf.ByteString.copyFrom(body)
        }.build()
        //如果发送失败，尝试发送三遍
        message.sent = IM_SEND_REQUEST
        KtChatDatabase.getInstance(Utils.getApp()).messageDao().updateMessage(message)
        resendMap[imCoreMessage.seqId] = IMCoroutineScope.launch {
            //如果在发送中，最多尝试3次
            while (message.sent == IM_SEND_REQUEST && message.retryCount < IM_RETRY_TIMES) {
                delay(1500)
                if (message.retryCount < IM_RETRY_TIMES) {
                    message.retryCount++
                    if (message.uuid == 0L) {
                        //获取uuid
                        val uuid = generateUid()
                        logger.debug { "retryWhenSendFailed: uuid $uuid" }
                        if (uuid == null) {
                            continue
                        }
                        message.uuid = uuid
                        KtChatDatabase.getInstance(Utils.getApp()).messageDao()
                            .updateMessage(message)
                    }
                    val result = IMWebSocket.send(imCoreMessage.toByteArray().toByteString())
                    logger.debug { "retryWhenSendFailed: retry ${message.retryCount} $result" }
                    //如果成功则更新状态为已发送
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
                                logger.debug { "retryWhenSendFailed: retry ${message.retryCount} but failed" }
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

    private fun handleSendMessageQueue() {
        IMCoroutineScope.launch {
            for (message in sendMessageChannel) {
                doSendMessage(message)//处理队列消息
            }
        }
    }


    override fun resendMessage(message: Message) {
        IMCoroutineScope.launch {
            doSendMessage(message)//重发
        }
    }

    override fun onReceiveMessage(bytes: ByteArray) {
        IMCoroutineScope.launch {
            receiveMessageChannel.send(bytes)
        }
    }

    override fun getOfflineMessage(fromId: String) {
        IMCoroutineScope.launch {
            try {
                IMClient.authService.getLoginData()?.let { it ->
                    //分页获取离线消息
                    val pageIndex = 0
                    val pageSize = 20
                    val lastMessage = KtChatDatabase.getInstance(Utils.getApp()).messageDao()
                        .lastMessage(fromId = fromId.toLong(), toId = it.user.uuid, 0)
                    logger.debug { "getOfflineMessage: token ${it.getApiToken()}" }
                    logger.debug { "getOfflineMessage: lastMessage uuid ${lastMessage?.uuid}" }
                    val apiResult =
                        IMHttpManger.imApiService.offlineMsg(
                            token = it.getApiToken(),
                            fromId = fromId,
                            messageId = lastMessage?.uuid ?: 0L,
                            pageIndex = pageIndex,
                            pageSize = pageSize
                        )
                    logger.debug { "getOfflineMessage: apiResult $apiResult" }
                    apiResult.data?.let {
                        Result.Success(it)
                        //直接保存到数据
                        for (message in it.messages) {
                            onReceiveMessage(message.toByteArray(StandardCharsets.ISO_8859_1))
                        }
                    } ?: Result.Error(IMException.ApiException(apiResult.message))
                } ?: Result.Error(IMException.TokenInvalidException)
            } catch (e: Throwable) {
                e.printStackTrace()
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

    private fun handleReceiveMessage() {
        IMCoroutineScope.launch {
            for (byteArray in receiveMessageChannel) {
                logger.debug { "bytes: $byteArray" }
                try {
                    //处理收到的消息
                    val imCoreMessage = IMCoreMessage.parseFrom(byteArray)
                    logger.debug { "handleReceiveMessage imCoreMessage $imCoreMessage" }
                    val imCoreMessageBody = imCoreMessage.bodyData.toStringUtf8()
                    logger.debug { "handleReceiveMessage imCoreMessageBody $imCoreMessageBody" }
                    when (imCoreMessage.cmd) {
                        "chat" -> {
                            val chatMessage = imCoreMessageBody.parseChatMessage()
                            logger.debug { "handleReceiveMessage chatMessage $chatMessage" }
                            when (imCoreMessage.subCmd) {
                                "text" -> {
                                    //acknowledge（发送方处理）
                                    if (imCoreMessage.type == 1) {
                                        logger.debug { "handleReceiveMessage cancel ${imCoreMessage.seqId} job and remove!" }
                                        //移除ACK队列中的超时任务
                                        ackMap[imCoreMessage.seqId]?.cancel()
                                        logger.debug { "handleReceiveMessage: ${imCoreMessage.seqId} " + ackMap[imCoreMessage.seqId] }
                                        ackMap.remove(imCoreMessage.seqId)
                                        //更新状态
                                        KtChatDatabase.getInstance(Utils.getApp()).messageDao()
                                            .queryMessage(
                                                imCoreMessage.seqId,
                                                chatMessage.fromId,
                                                chatMessage.toId,
                                                chatMessage.chatType
                                            )?.let {
                                                logger.debug { "handleReceiveMessage: update before $it" }
                                                it.sent = IM_SEND_RESPONSE
                                                logger.debug { "handleReceiveMessage: update after $it" }
                                                KtChatDatabase.getInstance(Utils.getApp())
                                                    .messageDao().updateMessage(it)
                                            }
                                    }
                                    //notify（接收方处理）
                                    if (imCoreMessage.type == 2) {
                                        //去重
                                        KtChatDatabase.getInstance(Utils.getApp()).messageDao()
                                            .queryMessage(
                                                imCoreMessage.seqId,
                                                chatMessage.fromId,
                                                chatMessage.toId,
                                                chatMessage.chatType
                                            )?.let {
                                                logger.debug { "handleReceiveMessage: update before $it" }
                                                it.sent = IM_SEND_RESPONSE
                                                logger.debug { "handleReceiveMessage: update after $it" }
                                                KtChatDatabase.getInstance(Utils.getApp())
                                                    .messageDao().updateMessage(it)
                                            } ?: let {
                                            val currentTime = System.currentTimeMillis()
                                            val message = Message(
                                                0,
                                                uuid = imCoreMessage.seqId,
                                                fromId = chatMessage.fromId,
                                                toId = chatMessage.toId,
                                                chatType = chatMessage.chatType,
                                                msgContent = imCoreMessageBody,
                                                msgTime = chatMessage.timestamp,
                                                createTime = currentTime,
                                                updateTime = currentTime,
                                            )
                                            KtChatDatabase.getInstance(Utils.getApp()).messageDao()
                                                .insertMessage(message)
                                        }
                                        //单聊，如果没有会话，则创建
                                        if (chatMessage.chatType == 0) {
                                            IMClient.conversationService.insertOrReplaceConversation(
                                                chatMessage.fromId,
                                                chatMessage.chatType
                                            )
                                        }
                                        //回调消息
                                        IMClient.onMessageCallback?.onReceiveMessage(chatMessage)
                                    }
                                }
                            }
                        }
                        //视频通话
                        "signaling" -> {
                            val signaling = imCoreMessageBody.parseSignaling()
                            logger.debug { "handleReceiveMessage signaling $signaling" }
                            SignalingServiceImpl.onReceiveSignaling(imCoreMessage, signaling)
                        }
                    }

                } catch (e: Exception) {
                    logger.error(e) { "handleReceiveMessage imCoreMessage ${e.message}" }
                }
            }
        }
    }
}