package com.nxg.im.core.http

import com.nxg.im.core.data.db.entity.Friend
import com.nxg.im.core.module.auth.LoginData
import com.nxg.im.core.module.auth.RegisterData
import com.nxg.im.core.module.user.User
import com.nxg.mvvm.http.ApiResult
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface IMApiService {

    @POST("/api/v1/register")
    suspend fun register(@Body requestBody: RequestBody): ApiResult<RegisterData>

    @POST("/api/v1/login")
    suspend fun login(@Body requestBody: RequestBody): ApiResult<LoginData>

    @POST("/api/v1/loginOut")
    suspend fun loginOut(@Body requestBody: RequestBody): ApiResult<Nothing>

    @GET("/api/v1/me")
    suspend fun me(@Header("Authorization") token: String): ApiResult<User>

    @GET("/api/v1/myFriends")
    suspend fun myFriends(@Header("Authorization") token: String): ApiResult<List<Friend>>

}