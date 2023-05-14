package com.nxg.im.core.module.user

import com.nxg.im.core.IMConstants
import com.nxg.im.core.data.Result
import com.nxg.im.core.exception.IMException
import com.nxg.im.core.http.IMHttpManger
import com.nxg.im.core.module.auth.AuthServiceImpl
import com.nxg.mvvm.logger.SimpleLogger
import java.io.IOException

class UserServiceImpl private constructor() : UserService, SimpleLogger {

    companion object {
        val instance by lazy {
            UserServiceImpl()
        }
    }

    override suspend fun me(): Result<User> {
        try {
            AuthServiceImpl.instance.loginData?.let {
                val apiResult =
                    IMHttpManger.imApiService.me("${IMConstants.Api.Bearer} ${it.token}")
                logger.debug { "me: apiResult $apiResult" }
                apiResult.data?.let {
                    return Result.Success(it)
                }
                return Result.Error(IMException.ApiException(apiResult.message))
            }
            return Result.Error(IMException.TokenInvalidException)
        } catch (e: Throwable) {
            e.printStackTrace()
            return Result.Error(IOException("${e.message}", e))
        }
    }

    override suspend fun updateUserInfo(): Result<User> {
        TODO("Not yet implemented")
    }
}