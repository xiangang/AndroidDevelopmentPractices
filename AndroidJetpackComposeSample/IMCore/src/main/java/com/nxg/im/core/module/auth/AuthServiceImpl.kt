package com.nxg.im.core.module.auth

import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.Utils
import com.nxg.im.core.IMConstants.Api.Me
import com.nxg.im.core.IMConstants.Api.MediaTypeJson
import com.nxg.im.core.IMConstants.Api.SecretSharedPrefs
import com.nxg.im.core.IMConstants.Api.Token
import com.nxg.im.core.http.IMHttpManger
import com.nxg.im.core.data.bean.Result
import com.nxg.im.core.http.IMWebSocket
import com.nxg.mvvm.logger.SimpleLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

object AuthServiceImpl : AuthService, SimpleLogger {

    private val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
    private val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)
    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        SecretSharedPrefs,
        masterKeyAlias,
        Utils.getApp().applicationContext,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    // in-memory cache of the loggedInUser object
    private var userLoginData: LoginData? = null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore

    }

    override suspend fun init() = withContext(Dispatchers.IO) {
        try {
            val loginDataJson = sharedPreferences.getString(Me, "")
            logger.debug { "loginDataJson: $loginDataJson" }
            if (loginDataJson != null && loginDataJson.isNotEmpty()) {
                userLoginData = GsonUtils.fromJson(loginDataJson, LoginData::class.java)
            }
            IMWebSocket.init()
        } catch (e: Exception) {
            logger.error(e.message)
        }
    }

    override suspend fun register(username: String, password: String): Result<RegisterData> {
        try {
            val requestBody = GsonUtils.toJson(LoginForm(username, password))
                .toRequestBody(MediaTypeJson.toMediaTypeOrNull())
            val apiResult = IMHttpManger.imApiService.register(requestBody)
            logger.debug { "register: apiResult $apiResult" }
            apiResult.data?.let {
                return Result.Success(it)
            }
            return Result.Error(Exception(apiResult.message))
        } catch (e: Throwable) {
            e.printStackTrace()
            return Result.Error(IOException("${e.message}", e))
        }
    }

    override suspend fun login(username: String, password: String): Result<LoginData> {
        try {
            rememberUserName(username)
            rememberPassword(password)
            val requestBody = GsonUtils.toJson(LoginForm(username, password))
                .toRequestBody(MediaTypeJson.toMediaTypeOrNull())
            val apiResult = IMHttpManger.imApiService.login(requestBody)
            logger.debug { "login: apiResult $apiResult" }
            apiResult.data?.let {
                saveLoginData(it)
                return Result.Success(it)
            }
            return Result.Error(Exception(apiResult.message))
        } catch (e: Throwable) {
            e.printStackTrace()
            return Result.Error(IOException("${e.message}", e))
        }
    }

    override suspend fun logout() {
        userLoginData?.let {
            userLoginData = null
            saveLoginData(null)
            saveApiToken("")
            //TODO 暂时不需要通过服务器接口来登出
            /*try {
                val requestBody = GsonUtils.toJson(LoginOutForm(it.token))
                    .toRequestBody(MediaTypeJson.toMediaTypeOrNull())
                val apiResult = IMHttpManger.imApiService.loginOut(requestBody)
                logger.debug { "logout: apiResult $apiResult" }
            } catch (e: Throwable) {
                e.printStackTrace()
            }*/
        }
    }

    override suspend fun isLoggedIn(): Boolean = userLoginData != null

    override suspend fun saveLoginData(loginData: LoginData?) {
        this.userLoginData = loginData
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        loginData?.let {
            editor.putString(Me, GsonUtils.toJson(it))
        } ?: let {
            editor.putString(Me, "")
        }
        editor.apply()
    }

    override fun getLoginData(): LoginData? {
        return userLoginData
    }

    override suspend fun saveApiToken(token: String) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(Token, token)
        editor.apply()
    }

    override fun getApiToken(): String? {
        return userLoginData?.getApiToken()
    }

    override suspend fun getWebSocketToken(): String? {
        return userLoginData?.token ?: let { null }
    }

    override suspend fun rememberUserName(username: String) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.apply()
    }

    override suspend fun rememberPassword(password: String) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("password", password)
        editor.apply()
    }

    override suspend fun getUsername(): String {
        return sharedPreferences.getString("username", "") ?: ""
    }

    override suspend fun getPassword(): String {
        return sharedPreferences.getString("password", "") ?: ""
    }
}