package com.nxg.im.core.module.signaling

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
sealed class SignalingType {
    @Serializable
    @SerialName("VideoCall")
    object VideoCall : SignalingType()

    @Serializable
    @SerialName("AudioCall")
    object AudioCall : SignalingType()
}

@Serializable
@SerialName("Signaling")
data class Signaling constructor(
    val id: Long, //整个会话session期间的唯一id
    val fromId: Long, //用户id
    val signalingType: SignalingType, //信令类型：video_call，audio_call
    val cmd: String, //信令命令：invite，cancel，answer，bye，
    val participants: Set<Long>,
    val onlineUserOnly: Boolean,
    val offlinePushInfo: String,
    val timeout: Int,
    val createTime: Long,
    val payload: String
) {
    fun cancel(fromId: Long): Signaling = copy(fromId = fromId, cmd = "cancel")
    fun answer(fromId: Long): Signaling = copy(fromId = fromId, cmd = "answer")
    fun bye(fromId: Long): Signaling = copy(fromId = fromId, cmd = "bye")

    override fun toString(): String {
        return toJson()
    }
}

fun Signaling.toJson(): String = Json.encodeToString(Signaling.serializer(), this)

fun String.parseSignaling(): Signaling = Json.decodeFromString(Signaling.serializer(), this)


object SignalingHelper {

    fun createVideoCallInvite(
        id: Long,
        fromId: Long,
        participants: Set<Long>,
        payload: String = ""
    ): Signaling {
        return Signaling(
            id,
            fromId,
            SignalingType.VideoCall,
            "invite",
            participants,
            true,
            "",
            10,
            System.currentTimeMillis(),
            payload
        )
    }

}