package com.nxg.im.core.module.upload

import android.content.Context
import com.nxg.im.core.IMService

interface UploadService : IMService {

    fun init(context: Context)

    fun syncUpload(filePath: String): String?

    fun asyncUpload(filePath: String)

    fun setUploadingFileProgress(filePath: String, progress: Int)

    fun getUploadingFileProgress(filePath: String): Int

    fun finishUploadFile(filePath: String)
}

//object UploadServiceMinio : UploadService, SimpleLogger {
//
//    // Create a minioClient with the MinIO server playground, its access key and secret key.
//    private val minioClient: MinioClient by lazy {
//        MinioClient.builder()
//            .endpoint(ENDPOINT)
//            .credentials(ACCESS_KEY, SECRET_KEY)
//            .build()
//    }
//
//    override fun init(context: Context) {
//        try {
//            // Make bucket if not exist.
//            val found =
//                minioClient.bucketExists(BucketExistsArgs.builder().bucket(BUCKET_NAME).build())
//            if (!found) {
//                // Make a new bucket called BUCKET
//                minioClient.makeBucket(MakeBucketArgs.builder().bucket(BUCKET_NAME).build())
//            } else {
//                logger.debug { "Bucket $BUCKET_NAME already exists." }
//            }
//        } catch (e: MinioException) {
//            logger.debug { "Error occurred: $e" }
//            logger.debug { "HTTP trace: " + e.httpTrace() }
//        }
//    }
//
//    override fun upload(filePath: String) {
//        try {
//            IMCoroutineScope.launch {
//                withContext(Dispatchers.IO) {
//                    val file = File(filePath)
//                    if (file.exists() && file.isFile) {
//                        minioClient.uploadObject(
//                            UploadObjectArgs.builder()
//                                .bucket(BUCKET_NAME)
//                                .`object`(file.name)
//                                .filename(file.name)
//                                .build()
//                        )
//                        logger.debug {
//                            "$filePath is successfully uploaded as object ${file.name} to bucket $BUCKET_NAME."
//                        }
//                    }
//                }
//            }
//
//        } catch (e: MinioException) {
//            logger.debug { "Error occurred: $e" }
//            logger.debug { "HTTP trace: " + e.httpTrace() }
//        }
//    }
//}