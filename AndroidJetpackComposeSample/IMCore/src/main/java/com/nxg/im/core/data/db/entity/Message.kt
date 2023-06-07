package com.nxg.im.core.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nxg.im.core.data.bean.IMMessage
import com.nxg.im.core.data.bean.parseIMMessage

@Entity
data class Message(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long,//当前数据库的自增长id
    @ColumnInfo(name = "uuid") val uuid: Long,//服务器的uuid，本地记录可为空
    @ColumnInfo(name = "from_id") val fromId: Long,//当前用户ID
    @ColumnInfo(name = "to_id") val toId: Long,//聊天ID，单聊时是用户ID，群聊是是群ID
    @ColumnInfo(name = "chat_type") val chatType: Int,//聊天类型：0单聊，1群聊
    @ColumnInfo(name = "msg_content") val msgContent: String,//消息的内容（json
    @ColumnInfo(name = "msg_at") val msgAt: String = "",//@对象（json）
    @ColumnInfo(name = "sent") val sent: Int = 0,//0未发送，1已发送
    @ColumnInfo(name = "status") val status: Int = 0,//0未读，1已读，2伪删除
    @ColumnInfo(name = "create_time") val createTime: Long = System.currentTimeMillis(),//创建时间
) {
    fun toIMMessage(): IMMessage {
        return msgContent.parseIMMessage()
    }
}