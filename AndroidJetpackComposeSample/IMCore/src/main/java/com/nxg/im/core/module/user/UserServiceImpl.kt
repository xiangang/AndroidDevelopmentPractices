package com.nxg.im.core.module.user

import com.nxg.im.core.IMClient
import com.nxg.im.core.data.Friend
import com.nxg.im.core.data.Result
import com.nxg.im.core.exception.IMException
import com.nxg.im.core.http.IMHttpManger
import com.nxg.mvvm.logger.SimpleLogger
import java.io.IOException

object UserServiceImpl : UserService, SimpleLogger {

    override suspend fun me(): Result<User> {
        return try {
            IMClient.authService.getApiToken()?.let { it ->
                val apiResult =
                    IMHttpManger.imApiService.me(it)
                logger.debug { "me: apiResult $apiResult" }
                apiResult.data?.let {
                    Result.Success(it)
                } ?: Result.Error(IMException.ApiException(apiResult.message))
            } ?: Result.Error(IMException.TokenInvalidException)
        } catch (e: Throwable) {
            e.printStackTrace()
            Result.Error(IOException("${e.message}", e))
        }
    }

    override suspend fun myFriends(): Result<List<Friend>> {
        try {
            IMClient.authService.getApiToken()?.let { it ->
                val apiResult =
                    IMHttpManger.imApiService.myFriends(it)
                logger.debug { "myFriends: apiResult $apiResult" }
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