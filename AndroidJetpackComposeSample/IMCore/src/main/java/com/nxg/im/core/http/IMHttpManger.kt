package com.nxg.im.core.http

import com.google.gson.GsonBuilder
import com.nxg.im.core.IMConstants
import com.nxg.mvvm.http.HttpsCerUtils
import com.nxg.mvvm.logger.SimpleLogger
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * IMHttpManger
 */
object IMHttpManger : SimpleLogger {

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
     * 构建IM服务器OkHttpClient实例
     */
    val imOkHttpClient: OkHttpClient by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
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
     * 构建IM服务器Retrofit实例
     */
    private val imRetrofit: Retrofit by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        val gson = GsonBuilder().serializeNulls().disableHtmlEscaping().create()
        val baseUrl = IMConstants.IM_SERVER_HTTP
        logger.debug { "imRetrofit baseUrl = $baseUrl" }
        Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(imOkHttpClient)
            .build()
    }

    /**
     * 获取IM服务器APIService实例
     */
    val imApiService: IMApiService by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        imRetrofit.create(IMApiService::class.java)
    }

    val imSignalingOkHttpClient: OkHttpClient by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
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