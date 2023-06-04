package com.nxg.im.core.module.auth

import com.nxg.im.core.IMService
import com.nxg.im.core.data.bean.Result

interface AuthService : IMService {

    suspend fun init()

    suspend fun register(username: String, password: String): Result<RegisterData>

    suspend fun login(username: String, password: String): Result<LoginData>

    suspend fun logout()

    suspend fun isLoggedIn(): Boolean

    suspend  fun saveLoginData(loginData: LoginData? = null)

    suspend fun getLoginData(): LoginData?

    suspend fun saveUserToken(token: String)

    suspend fun getApiToken(): String?

    suspend fun getWebSocketToken(): String?
}