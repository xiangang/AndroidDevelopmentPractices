package com.nxg.im.core.module.auth

import com.nxg.im.core.IMService
import com.nxg.im.core.data.bean.Result

interface AuthService : IMService {

    suspend fun init()

    suspend fun register(username: String, password: String): Result<RegisterData>

    suspend fun login(username: String, password: String): Result<LoginData>

    suspend fun logout()

    suspend fun isLoggedIn(): Boolean

    suspend fun saveLoginData(loginData: LoginData? = null)

    fun getLoginData(): LoginData?

    suspend fun saveApiToken(token: String)

    fun getApiToken(): String?

    suspend fun getWebSocketToken(): String?

    suspend fun rememberUserName(username: String)

    suspend fun rememberPassword(password: String)

    suspend fun getUsername(): String

    suspend fun getPassword(): String
}