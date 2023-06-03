package com.nxg.im.core.data

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
sealed class IMMessage {
    abstract val from_id: Long
    abstract val to_id: Long
    abstract val chat_type: Int
    abstract val content: MessageContent
    abstract val timestamp: String
}

@Serializable
data class TextMessage(
    override val from_id: Long,
    override val to_id: Long,
    override val chat_type: Int,
    override val content: TextMsgContent,
    override val timestamp: String
) : IMMessage()

@Serializable
data class ImageMessage(
    override val from_id: Long,
    override val to_id: Long,
    override val chat_type: Int,
    override val content: ImageMsgContent,
    override val timestamp: String
) : IMMessage()

@Serializable
data class AudioMessage(
    override val from_id: Long,
    override val to_id: Long,
    override val chat_type: Int,
    override val content: AudioMsgContent,
    override val timestamp: String
) : IMMessage()

@Serializable
data class VideoMessage(
    override val from_id: Long,
    override val to_id: Long,
    override val chat_type: Int,
    override val content: VideoMsgContent,
    override val timestamp: String
) : IMMessage()

@Serializable
data class FileMessage(
    override val from_id: Long,
    override val to_id: Long,
    override val chat_type: Int,
    override val content: FileMsgContent,
    override val timestamp: String
) : IMMessage()

@Serializable
data class LocationMessage(
    override val from_id: Long,
    override val to_id: Long,
    override val chat_type: Int,
    override val content: LocationMsgContent,
    override val timestamp: String
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
data class VideoMsgContent(val url: String, val duration: Int, val width: Int, val height: Int) : MessageContent()

@Serializable
data class FileMsgContent(val url: String, val name: String, val size: Int) : MessageContent()

@Serializable
data class LocationMsgContent(val latitude: Double, val longitude: Double, val address: String) : MessageContent()

fun IMMessage.toJson(): String = Json.encodeToString(IMMessage.serializer(), this)

fun String.parseIMMessage(): IMMessage = Json.decodeFromString(IMMessage.serializer(), this)