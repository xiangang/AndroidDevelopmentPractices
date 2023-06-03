package com.nxg.im.core.data

import com.nxg.im.core.module.user.User

data class Friend(
    val user: User,
    val groupId: Int,
    val isRecommend: Int,
    val latestDynamic: String,
    val permission: Int,
    val relationStatus: Int,
    val relationType: Int,
    val remark: String,
    val createTime: String,
    val updateTime: String
)