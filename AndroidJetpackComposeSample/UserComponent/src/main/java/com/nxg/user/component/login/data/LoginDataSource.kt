package com.nxg.user.component.login.data

import com.blankj.utilcode.util.GsonUtils
import com.nxg.im.http.IMHttpManger
import com.nxg.im.http.bean.LoginData
import com.nxg.mvvm.logger.SimpleLogger
import com.nxg.user.component.login.ui.login.LoginForm
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource : SimpleLogger {

    suspend fun login(username: String, password: String): Result<LoginData> {
        try {
            val requestBody = GsonUtils.toJson(LoginForm(username, password))
                .toRequestBody("application/json".toMediaTypeOrNull())
            val apiResult = IMHttpManger.imApiService.login(requestBody)
            logger.debug { "login: apiResult $apiResult" }
            apiResult.data?.let {
                return Result.Success(it)
            }
            return Result.Error(Exception(apiResult.message))
        } catch (e: Throwable) {
            e.printStackTrace()
            return Result.Error(IOException("${e.message}", e))
        }
    }

    suspend fun logout() {

    }
}