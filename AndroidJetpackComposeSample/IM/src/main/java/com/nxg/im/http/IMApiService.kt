package com.nxg.im.http

import com.nxg.im.http.bean.LoginData
import com.nxg.im.http.bean.RegisterData
import com.nxg.mvvm.http.ApiResult
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface IMApiService {

    @POST("/api/v1/register")
    suspend fun register(@Body requestBody: RequestBody): ApiResult<RegisterData>

    @POST("/api/v1/login")
    suspend fun login(@Body requestBody: RequestBody): ApiResult<LoginData>

}