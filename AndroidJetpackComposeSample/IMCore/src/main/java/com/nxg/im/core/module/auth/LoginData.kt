package com.nxg.im.core.module.auth

import com.nxg.im.core.IMConstants
import com.nxg.im.core.module.user.User


data class LoginData(val token: String, val user: User) {
    /**
     * 请求Http接口请使用此方法返回Bearer auth认证的token
     */
    fun getApiToken(): String {
        return token.let {
            "${IMConstants.Api.Bearer} $it"
        }
    }
}