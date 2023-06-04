package com.nxg.im.core.data.bean

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
sealed class IMMessage {
    abstract val fromId: Long
    abstract val toId: Long
    abstract val chatType: Int
    abstract val content: MessageContent
    abstract val timestamp: Long
}

@Serializable
data class TextMessage(
    override val fromId: Long,
    override val toId: Long,
    override val chatType: Int,
    override val content: TextMsgContent,
    override val timestamp: Long
) : IMMessage()

@Serializable
data class ImageMessage(
    override val fromId: Long,
    override val toId: Long,
    override val chatType: Int,
    override val content: ImageMsgContent,
    override val timestamp: Long
) : IMMessage()

@Serializable
data class AudioMessage(
    override val fromId: Long,
    override val toId: Long,
    override val chatType: Int,
    override val content: AudioMsgContent,
    override val timestamp: Long
) : IMMessage()

@Serializable
data class VideoMessage(
    override val fromId: Long,
    override val toId: Long,
    override val chatType: Int,
    override val content: VideoMsgContent,
    override val timestamp: Long
) : IMMessage()

@Serializable
data class FileMessage(
    override val fromId: Long,
    override val toId: Long,
    override val chatType: Int,
    override val content: FileMsgContent,
    override val timestamp: Long
) : IMMessage()

@Serializable
data class LocationMessage(
    override val fromId: Long,
    override val toId: Long,
    override val chatType: Int,
    override val content: LocationMsgContent,
    override val timestamp: Long
) : IMMessage()

@Serializable
sealed class MessageContent

fun MessageContent.toJson(): String = Json.encodeToString(MessageContent.serializer(), this)

@Serializable
data class TextMsgContent(val text: String) : MessageContent()

@Serializable
data class ImageMsgContent(val url: String, val width: Int, val height: Int) : MessageContent()

@Serializable
data class AudioMsgContent(val url: String, val duration: Int) : MessageContent()

@Serializable
data class VideoMsgContent(val url: String, val duration: Int, val width: Int, val height: Int) :
    MessageContent()

@Serializable
data class FileMsgContent(val url: String, val name: String, val size: Int) : MessageContent()

@Serializable
data class LocationMsgContent(val latitude: Double, val longitude: Double, val address: String) :
    MessageContent()

fun IMMessage.toJson(): String = Json.encodeToString(IMMessage.serializer(), this)

fun String.parseIMMessage(): IMMessage = Json.decodeFromString(IMMessage.serializer(), this)