package com.nxg.mvvm.http

import android.content.Context
import okhttp3.OkHttpClient
import java.io.ByteArrayInputStream
import java.lang.Exception
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.*

object HttpsCerUtils {

    //信任所有证书
    fun setTrustAllCertificate(okHttpClientBuilder: OkHttpClient.Builder) {
        try {
            val sc = SSLContext.getInstance("TLSv1.2")
            val trustAllManager: X509TrustManager = object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            }
            sc.init(null, arrayOf<TrustManager>(trustAllManager), SecureRandom())
            okHttpClientBuilder.sslSocketFactory(sc.socketFactory, trustAllManager)
            //如果需要兼容安卓5.0以下，可以使用这句
            //okHttpClientBuilder.sslSocketFactory(new TLSSocketFactory(), trustAllManager);
            okHttpClientBuilder.hostnameVerifier(HostnameVerifier { hostname, session -> true })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //只信任指定证书（传入字符串）
    fun setCertificate(
        context: Context,
        okHttpClientBuilder: OkHttpClient.Builder,
        cerStr: String
    ) {
        try {
            val certificateFactory = CertificateFactory.getInstance("X.509")
            val byteArrayInputStream = ByteArrayInputStream(cerStr.toByteArray())
            val ca = certificateFactory.generateCertificate(byteArrayInputStream)
            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
            keyStore.load(null, null)
            keyStore.setCertificateEntry("ca", ca)
            byteArrayInputStream.close()
            val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            tmf.init(keyStore)
            val sslContext = SSLContext.getInstance("TLSv1.2")
            sslContext.init(null, tmf.trustManagers, SecureRandom())
            okHttpClientBuilder.sslSocketFactory(
                sslContext.socketFactory,
                tmf.trustManagers[0] as X509TrustManager
            )
            okHttpClientBuilder.hostnameVerifier(HostnameVerifier { hostname, session -> true })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //只信任指定证书（传入raw资源ID）
    fun setCertificate(context: Context, okHttpClientBuilder: OkHttpClient.Builder, cerResID: Int) {
        try {
            val certificateFactory = CertificateFactory.getInstance("X.509")
            val inputStream = context.resources.openRawResource(cerResID)
            val ca = certificateFactory.generateCertificate(inputStream)
            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
            keyStore.load(null, null)
            keyStore.setCertificateEntry("ca", ca)
            inputStream.close()
            val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            tmf.init(keyStore)
            val sslContext = SSLContext.getInstance("TLSv1.2")
            sslContext.init(null, tmf.trustManagers, SecureRandom())
            okHttpClientBuilder.sslSocketFactory(
                sslContext.socketFactory,
                tmf.trustManagers[0] as X509TrustManager
            )
            okHttpClientBuilder.hostnameVerifier(HostnameVerifier { hostname, session -> true })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //批量信任证书
    fun setCertificates(
        context: Context,
        okHttpClientBuilder: OkHttpClient.Builder,
        vararg cerResIDs: Int
    ) {
        try {
            val certificateFactory = CertificateFactory.getInstance("X.509")
            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
            keyStore.load(null, null)
            for (i in 0 until cerResIDs.size) {
                val ca = certificateFactory.generateCertificate(
                    context.resources.openRawResource(
                        cerResIDs[i]
                    )
                )
                keyStore.setCertificateEntry("ca$i", ca)
            }
            val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            tmf.init(keyStore)
            val sslContext = SSLContext.getInstance("TLSv1.2")
            sslContext.init(null, tmf.trustManagers, SecureRandom())
            okHttpClientBuilder.sslSocketFactory(
                sslContext.socketFactory,
                tmf.trustManagers[0] as X509TrustManager
            )
            okHttpClientBuilder.hostnameVerifier(HostnameVerifier { hostname, session -> true })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}