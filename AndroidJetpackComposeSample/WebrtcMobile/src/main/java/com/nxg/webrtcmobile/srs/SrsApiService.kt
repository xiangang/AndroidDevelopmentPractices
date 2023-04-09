package com.nxg.webrtcmobile.srs

import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface SrsApiService {

    @POST("/rtc/v1/publish/")
    suspend fun publish(@Body requestBody: RequestBody): SrsResponseBean

    @POST("/rtc/v1/play/")
    suspend fun play(@Body requestBody: RequestBody): SrsResponseBean

}