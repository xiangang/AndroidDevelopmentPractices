package com.nxg.im.core.module.auth

import com.nxg.im.core.module.user.User


data class LoginData(val token: String,val user: User)