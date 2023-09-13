package com.nxg.im.core.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.nxg.im.core.data.bean.IMMessage
import com.nxg.im.core.data.bean.MessageContent
import com.nxg.im.core.data.bean.parseIMMessage
import com.nxg.im.core.data.bean.parseMessageContent

typealias IMSendStatus = Int

const val IM_SEND_DEFAULT: IMSendStatus = 0 //默认已发送
const val IM_SEND_REQUEST: IMSendStatus = 1 //发送中
const val IM_SEND_RESPONSE: IMSendStatus = 2 //收到服务器回复
const val IM_SEND_FAILED: IMSendStatus = 3 //发送失败（没有收到服务器回复）
const val IM_RETRY_TIMES = 3 //失败后尝试的次数

@Entity
data class Message constructor(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") var id: Long,//当前数据库的自增长id
    @ColumnInfo(name = "uuid") var uuid: Long,//服务器的uuid，本地记录可为空
    @ColumnInfo(name = "from_id") val fromId: Long,//当前用户ID
    @ColumnInfo(name = "to_id") val toId: Long,//聊天ID，单聊时是用户ID，群聊是是群ID
    @ColumnInfo(name = "chat_type") val chatType: Int,//聊天类型：0单聊，1群聊
    @ColumnInfo(name = "msg_content") val msgContent: String,//消息的内容（json
    @ColumnInfo(name = "msg_at") var msgAt: String = "",//@对象（json）
    @ColumnInfo(name = "msg_time") val msgTime: Long = System.currentTimeMillis(),//消息时间戳，如果是发送方的，则等于创建时间，如果是接收方则等于消息中的时间戳
    @ColumnInfo(name = "sent") var sent: Int = 0,//0发送成功，1发送中，2发送失败
    @ColumnInfo(name = "status") var status: Int = 0,//0未读，1已读，2伪删除
    @ColumnInfo(name = "create_time") val createTime: Long = System.currentTimeMillis(),//创建时间
    @ColumnInfo(name = "update_time") val updateTime: Long = System.currentTimeMillis(),//更新时间
) {
    @Ignore
    var retryCount = 0 //失败尝试次数

    fun toIMMessage(): IMMessage {
        return msgContent.parseIMMessage()
    }
}