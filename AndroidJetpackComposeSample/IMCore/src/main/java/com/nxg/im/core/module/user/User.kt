package com.nxg.im.core.module.user

data class User(
    val id: Int,
    val uuid: Long,
    val username: String,
    val password: String,
    val nickname: String,
    val email: String,
    val phone: String,
    val avatar: String,
    val address: String,
    val province: String,
    val city: String,
    val country: String,
    val status: Int,
    val createTime: String,
    val updateTime: String
)