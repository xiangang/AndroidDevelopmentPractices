package com.nxg.webrtcmobile.srs

import com.google.gson.GsonBuilder
import com.nxg.mvvm.http.HttpsCerUtils
import com.nxg.mvvm.logger.SimpleLogger
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object SrsHttpManger : SimpleLogger {

    /**
     * 打印retrofit日志
     */
    private val httpLoggingInterceptor: HttpLoggingInterceptor by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        val loggingInterceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                logger.debug { "message $message" }
            }

        }).apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        loggingInterceptor
    }

    /**
     * 构建SRS服务器OkHttpClient实例
     */
    private val srsOkHttpClient: OkHttpClient by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        /**OkHttpClient默认时间10秒 请求时间较长时，重新设置下  **/
        val builder = OkHttpClient.Builder().apply {
            connectTimeout(5, TimeUnit.SECONDS)
            readTimeout(5, TimeUnit.SECONDS)
            writeTimeout(5, TimeUnit.SECONDS)
            retryOnConnectionFailure(true)
            addInterceptor(httpLoggingInterceptor)//添加日志拦截器
        }
        HttpsCerUtils.setTrustAllCertificate(builder)
        builder.build()
    }

    /**
     * 构建SRS服务器Retrofit实例
     */
    private val srsRetrofit: Retrofit by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        val gson = GsonBuilder().serializeNulls().disableHtmlEscaping().create()
        val baseUrl = SrsConstant.SRS_SERVER_HTTPS
        logger.debug { "srsRetrofit baseUrl = $baseUrl" }
        Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(srsOkHttpClient)
            .build()
    }

    /**
     * 获取SRS服务器APIService实例
     */
    val srsApiService: SrsApiService by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        srsRetrofit.create(SrsApiService::class.java)
    }

    val srsSignalingOkHttpClient: OkHttpClient by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        /**OkHttpClient默认时间10秒 请求时间较长时，重新设置下  **/
        val builder = OkHttpClient.Builder().apply {
            pingInterval(10, TimeUnit.SECONDS) // 设置PING包发送间隔
            connectTimeout(5, TimeUnit.SECONDS)
            readTimeout(5, TimeUnit.SECONDS)
            writeTimeout(5, TimeUnit.SECONDS)
            retryOnConnectionFailure(true)
            addInterceptor(httpLoggingInterceptor)//添加日志拦截器
        }
        HttpsCerUtils.setTrustAllCertificate(builder)
        builder.build()
    }


}