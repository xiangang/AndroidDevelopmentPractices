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
@SerialName("Text")
data class TextMessage constructor(
    override val fromId: Long,
    override val toId: Long,
    override val chatType: Int,
    override val content: TextMsgContent,
    override val timestamp: Long
) : IMMessage()

@Serializable
@SerialName("Image")
data class ImageMessage(
    override val fromId: Long,
    override val toId: Long,
    override val chatType: Int,
    override val content: ImageMsgContent,
    override val timestamp: Long
) : IMMessage()

@Serializable
@SerialName("Audio")
data class AudioMessage(
    override val fromId: Long,
    override val toId: Long,
    override val chatType: Int,
    override val content: AudioMsgContent,
    override val timestamp: Long
) : IMMessage()

@Serializable
@SerialName("Video")
data class VideoMessage(
    override val fromId: Long,
    override val toId: Long,
    override val chatType: Int,
    override val content: VideoMsgContent,
    override val timestamp: Long
) : IMMessage()

@Serializable
@SerialName("File")
data class FileMessage(
    override val fromId: Long,
    override val toId: Long,
    override val chatType: Int,
    override val content: FileMsgContent,
    override val timestamp: Long
) : IMMessage()

@Serializable
@SerialName("Location")
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

fun String.parseMessageContent(): MessageContent = Json.decodeFromString(MessageContent.serializer(), this)

@Serializable
@SerialName("Text")
data class TextMsgContent(val text: String) : MessageContent()

@Serializable
@SerialName("Image")
data class ImageMsgContent(val url: String, val width: Int, val height: Int) : MessageContent()

@Serializable
@SerialName("Audio")
data class AudioMsgContent(val url: String, val duration: Int) : MessageContent()

@Serializable
@SerialName("Video")
data class VideoMsgContent(val url: String, val duration: Int, val width: Int, val height: Int) :
    MessageContent()

@Serializable
@SerialName("File")
data class FileMsgContent(val url: String, val name: String, val size: Int) : MessageContent()

@Serializable
@SerialName("Location")
data class LocationMsgContent(val latitude: Double, val longitude: Double, val address: String) :
    MessageContent()

fun IMMessage.toJson(): String = Json.encodeToString(IMMessage.serializer(), this)

fun String.parseIMMessage(): IMMessage = Json.decodeFromString(IMMessage.serializer(), this)