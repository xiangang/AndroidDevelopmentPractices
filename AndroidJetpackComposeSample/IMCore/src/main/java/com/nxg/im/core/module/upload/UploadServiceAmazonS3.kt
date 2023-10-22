//package com.nxg.im.core.module.upload
//
//import android.content.Context
//import com.amazonaws.ClientConfiguration
//import com.amazonaws.auth.BasicAWSCredentials
//import com.amazonaws.regions.Region
//import com.amazonaws.regions.Regions
//import com.amazonaws.services.s3.AmazonS3
//import com.amazonaws.services.s3.AmazonS3Client
//import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
//import com.amazonaws.services.s3.model.PutObjectRequest
//import com.nxg.im.core.dispatcher.IMCoroutineScope
//import com.nxg.mvvm.logger.SimpleLogger
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import java.io.File
//import java.net.URL
//
//private const val ACCESS_KEY = "5TUmmjEalS9uFP7gfkrI"
//private const val SECRET_KEY = "M2shnfVuGLYiLYw4bCw0If7GmxFTecsIlj1sIoEw"
//private const val BUCKET_NAME = "k-signaling"
//private const val ENDPOINT = "http://192.168.1.5:9000"
//
//object UploadServiceAmazonS3 : UploadService, SimpleLogger {
//
//    private val s3: AmazonS3 by lazy {
//        val basicAWSCredentials = BasicAWSCredentials(ACCESS_KEY, SECRET_KEY)
//        AmazonS3Client(
//            basicAWSCredentials,
//            Region.getRegion(Regions.CN_NORTH_1),
//            ClientConfiguration()
//        ).apply {
//            endpoint = ENDPOINT
//        }
//    }
//
//    override fun init(context: Context) {
//
//    }
//
//    override fun upload(filePath: String): String {
//        IMCoroutineScope.launch {
//            withContext(Dispatchers.IO) {
//                val file = File(filePath)
//                val urlRequest = GeneratePresignedUrlRequest(
//                    BUCKET_NAME, file.name
//                )
//                var bytesTransferred: Long = 0
//                //这里我们上传文件的时候bucketName就需要替换为oss，bucketName拼到文件名的前面
//                s3.putObject(
//                    PutObjectRequest(
//                        BUCKET_NAME,
//                        file.name,
//                        file
//                    ).withGeneralProgressListener {
//                        bytesTransferred += it.bytesTransferred
//                        val percent = (bytesTransferred.toFloat() / file.length())
//                        logger.debug { "upload withGeneralProgressListener $percent" }
//                    })
//                val url: URL = s3.generatePresignedUrl(urlRequest)
//                logger.debug { "upload: url $url" }
//            }
//        }
//        return ""
//    }
//}
//
//
