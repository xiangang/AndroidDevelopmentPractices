package com.nxg.im.core.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["user_id", "friend_id"])
data class Friend(
    @ColumnInfo(name = "user_id") val userId: Long,//所属用户ID
    @ColumnInfo(name = "friend_id") val friendId: Long,//好友ID
    @ColumnInfo(name = "username") val username: String,//好友用户名
    @ColumnInfo(name = "nickname") val nickname: String,//好友昵称
    @ColumnInfo(name = "email") val email: String,//邮箱
    @ColumnInfo(name = "phone") val phone: String,//手机号
    @ColumnInfo(name = "gender") val gender: Int,//性别：0未知，1男，2女
    @ColumnInfo(name = "avatar") val avatar: String,//头像
    @ColumnInfo(name = "address") val address: String,//地址
    @ColumnInfo(name = "province") val province: String,//省份
    @ColumnInfo(name = "city") val city: String,//城市
    @ColumnInfo(name = "status") val status: Int,//状态
    @ColumnInfo(name = "create_time") val createTime: Long,//创建时间
    @ColumnInfo(name = "update_time") val updateTime: Long,//创建时间
    @ColumnInfo(name = "group_id") val groupId: Int,//组id
    @ColumnInfo(name = "is_recommend") val isRecommend: Int,//是否推荐
    @ColumnInfo(name = "latest_dynamic") val latestDynamic: String,//动态
    @ColumnInfo(name = "permission") val permission: Int,//权限
    @ColumnInfo(name = "relation_status") val relationStatus: Int,//关系状态
    @ColumnInfo(name = "relation_type") val relationType: Int,//关系类型
    @ColumnInfo(name = "remark") val remark: String,//备注
)


