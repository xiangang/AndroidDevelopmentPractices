package com.nxg.im.conversation

import com.nxg.im.core.module.user.User

data class Group(val users: List<User>)

data class Conversation(
    val id: Long,
    val type: Int,
    val user: User? = null,
    val group: Group? = null
)