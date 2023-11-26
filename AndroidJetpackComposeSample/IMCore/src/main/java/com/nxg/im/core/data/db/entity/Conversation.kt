package com.nxg.im.core.data.db.entity

import androidx.room.*
import com.nxg.im.core.data.bean.ChatMessage

@Entity(primaryKeys = ["user_id", "chat_id", "chat_type"], indices = [Index(value = ["name"])])
data class Conversation(
    @ColumnInfo(name = "user_id") val userId: Long,//当前用户ID
    @ColumnInfo(name = "chat_id") val chatId: Long,//聊天ID，单聊时是用户ID，群聊是是群ID
    @ColumnInfo(name = "chat_type") val chatType: Int,//聊天类型：0单聊，1群聊
    @ColumnInfo(name = "name") val name: String,//会话名称
    @ColumnInfo(name = "cover_image") val coverImage: String,//封面图片
    @ColumnInfo(name = "background_image") val backgroundImage: String, //聊天背景图片
    @ColumnInfo(name = "last_msg_id") val lastMsgId: Long,//最后一个消息的ID
    @ColumnInfo(name = "last_msg_content") val lastMsgContent: String,//最后一个消息的内容
    @ColumnInfo(name = "create_ime") val createTime: Long,//创建时间
    @ColumnInfo(name = "update_time") val updateTime: Long,//更新时间
    @ColumnInfo(name = "unread_count") val unreadCount: Long,//未读消息数量
    @ColumnInfo(name = "draft") val draft: String,//草稿
    @ColumnInfo(name = "top") val top: Boolean, //指定聊天
    @ColumnInfo(name = "sticky") val sticky: Boolean, //消息免打扰
    @ColumnInfo(name = "remind") val remind: Boolean,//提醒
) {
    @Ignore
    var lastChatMessage: ChatMessage? = null//最后一个消息ChatMessage

    fun updateLastChatMessage() {
    }
}