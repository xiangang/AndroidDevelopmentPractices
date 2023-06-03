package com.nxg.im.conversation.component.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity


@Entity(primaryKeys = ["firstName", "lastName"])
data class Conversation(
    @ColumnInfo(name = "from_id") val fromId: Long,
    @ColumnInfo(name = "to_id") val toId: Long,
    @ColumnInfo(name = "chat_type") val chatType: Int,
)