package com.nxg.im.core.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.nxg.im.core.data.bean.IMMessage
import com.nxg.im.core.data.bean.parseIMMessage

@Entity(primaryKeys = ["from_id", "to_id", "chat_type"])
data class Message(
    @ColumnInfo(name = "from_id") val from_id: Long,//当前用户ID
    @ColumnInfo(name = "to_id") val to_id: Long,//聊天ID，单聊时是用户ID，群聊是是群ID
    @ColumnInfo(name = "chat_type") val chatType: Int,//聊天类型：0单聊，1群聊
    @ColumnInfo(name = "msg_content") val msgContent: String,//消息的内容（json
    @ColumnInfo(name = "msg_at") val msgAt: String,//@对象（json）
    @ColumnInfo(name = "status") val status: Int,//0未读，1已读，2伪删除
    @ColumnInfo(name = "create_time") val createTime: Long,//创建时间
){
    fun toIMMessage(): IMMessage {
        return msgContent.parseIMMessage()
    }
}