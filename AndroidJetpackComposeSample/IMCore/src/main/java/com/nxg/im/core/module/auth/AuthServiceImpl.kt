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
import com.nxg.im.core.data.Result
import com.nxg.mvvm.logger.SimpleLogger
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class AuthServiceImpl private constructor(): AuthService, SimpleLogger {

    companion object {
        val instance by lazy {
            AuthServiceImpl()
        }
    }

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
    var loginData: LoginData? = null
        private set

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        try {
            val loginDataJson = sharedPreferences.getString(Me, "")
            logger.debug { "loginDataJson: $loginDataJson" }
            if (loginDataJson != null && loginDataJson.isNotEmpty()) {
                loginData = GsonUtils.fromJson(loginDataJson, LoginData::class.java)
            }
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

    override suspend fun logout(token: String) {
        loginData?.let {
            try {
                val requestBody = GsonUtils.toJson(LoginOutForm(token))
                    .toRequestBody(MediaTypeJson.toMediaTypeOrNull())
                val apiResult = IMHttpManger.imApiService.loginOut(requestBody)
                logger.debug { "logout: apiResult $apiResult" }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    override suspend fun isLoggedIn(): Boolean = loginData != null

    override suspend fun saveLoginData(loginData: LoginData) {
        this.loginData = loginData
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(Me, GsonUtils.toJson(loginData))
        editor.apply()
    }

    override suspend fun getLoginData(): LoginData? {
        return loginData
    }

    override suspend fun saveUserToken(token: String) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(Token, token)
        editor.apply()
    }

    override suspend fun getUserToken(): String? {
        return loginData?.token ?: let { null }
    }
}