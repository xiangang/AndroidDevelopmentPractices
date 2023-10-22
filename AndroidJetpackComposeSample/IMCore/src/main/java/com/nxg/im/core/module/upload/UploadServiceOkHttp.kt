package com.nxg.im.core.module.upload

import android.content.Context
import com.nxg.im.core.IMClient
import com.nxg.im.core.dispatcher.IMCoroutineScope
import com.nxg.im.core.http.IMHttpManger
import com.nxg.im.core.module.auth.AuthService
import com.nxg.mvvm.http.ApiResult
import com.nxg.mvvm.logger.SimpleLogger
import com.nxg.mvvm.utils.GsonUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Companion.FORM
import okhttp3.Request
import okhttp3.RequestBody
import okio.Buffer
import okio.BufferedSink
import okio.source
import java.io.File
import java.util.concurrent.ConcurrentHashMap

object UploadServiceOkHttp : UploadService, SimpleLogger {

    /**
     * 缓存上传中文件进度的Map
     */
    private val uploadingFileProgressMap = ConcurrentHashMap<String, Int>()

    override fun init(context: Context) {

    }

    override fun syncUpload(filePath: String): String? {
        return multipartUploadProgress(
            "http://192.168.1.5:8050/api/v1/upload",
            filePath,
            "application/octet-stream".toMediaType()
        ) { s ->
            when (s) {
                UploadState.Complete -> {
                    logger.debug { "UploadService upload $filePath 上传完成" }
                    finishUploadFile(filePath)
                }

                UploadState.FileNotExist -> {
                    logger.debug { "UploadService upload $filePath 上传失败，文件不存在" }
                    finishUploadFile(filePath)
                }

                is UploadState.Progress -> {
                    val progress = (s.current.toFloat() / s.totalNum) * 100
                    if (s.current % 10 == 0L) {
                        logger.debug { "UploadService upload $filePath 上传中  ${progress}%" }
                    }
                    setUploadingFileProgress(filePath, progress.toInt())
                }

                UploadState.UnStart -> {
                    logger.debug { "UploadService upload $filePath 上传未开始" }
                    finishUploadFile(filePath)
                }

                is UploadState.Error -> {
                    logger.debug { "UploadService upload $filePath 上传失败  ${s.e.message}" }
                    finishUploadFile(filePath)
                }
            }
        }
    }

    override fun asyncUpload(filePath: String) {
        IMCoroutineScope.launch {
            withContext(Dispatchers.IO) {


            }
        }
    }

    private fun multipartUploadProgress(
        url: String,
        filePath: String,
        contentType: MediaType,
        params: Map<String, String>? = null,
        setState: (UploadState) -> Unit
    ): String? {
        val state: UploadState = UploadState.UnStart
        setState(state)
        val file = File(filePath)
        val body = MultipartBody.Builder()
            .also {
                params?.forEach { (k, v) ->
                    it.addFormDataPart(k, v)
                }
            }.also {
                if (file.exists()) {
                    it.addFormDataPart(
                        "file",
                        file.name,
                        file.asProgressRequestBody(contentType, setState)
                    )
                }
            }.setType(FORM).build()
        IMClient.getService<AuthService>().getApiToken()?.let {
            val request = Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", it)
                .build()
            try {
                logger.debug { "multipartUploadProgress: $filePath request $request" }
                logger.debug { "multipartUploadProgress: $filePath contentType ${request.body?.contentType()}" }
                logger.debug { "multipartUploadProgress: $filePath contentLength ${request.body?.contentLength()}" }
                val response = IMHttpManger.imOkHttpClient.newCall(request).execute()
                logger.debug { "multipartUploadProgress: $filePath response $response" }
                val responseBody = response.body?.string()
                logger.debug { "multipartUploadProgress: $filePath responseBody $responseBody" }
                if (!responseBody.isNullOrEmpty()) {
                    val result: ApiResult<String> =
                        GsonUtils.fromJson(responseBody, String::class.java)
                    logger.debug { "multipartUploadProgress: $filePath url ${result.data}" }
                    return result.data
                }

            } catch (e: Exception) {
                logger.debug { "multipartUploadProgress: $filePath ${e.message}" }
            }
        }
        return ""
    }

    /**
     * 带进度条上传的功能
     */
    private fun File.asProgressRequestBody(
        contentType: MediaType? = null,
        setState: (UploadState) -> Unit?
    ): RequestBody {
        return object : RequestBody() {

            override fun contentType() = contentType

            override fun contentLength() = length()

            override fun writeTo(sink: BufferedSink) {
                source().use { source ->
                    val buffer = Buffer()
                    var readCount = 0L
                    var progress = 0L
                    val progressBlock = UploadState.Progress(contentLength(), progress)
                    try {
                        do {
                            if (readCount != 0L) {
                                progress += readCount
                                progressBlock.current = progress
                                sink.write(buffer, readCount)
                                setState(progressBlock)
                            }
                            readCount = source.read(buffer, 2048)
                        } while (readCount != -1L)
                    } catch (e: Exception) {
                        setState(UploadState.Error(e))
                    }
                }
            }
        }
    }


    override fun setUploadingFileProgress(filePath: String, progress: Int) {
        logger.debug { "setUploadingFileProgress: $filePath -> $progress" }
        uploadingFileProgressMap[filePath] = progress
    }

    override fun getUploadingFileProgress(filePath: String): Int {
        val progress = uploadingFileProgressMap[filePath] ?: -1
        logger.debug { "getUploadingFileProgress: $filePath -> $progress" }
        return uploadingFileProgressMap[filePath] ?: -1
    }

    override fun finishUploadFile(filePath: String) {
        logger.debug { "finishUploadFile: $filePath " }
        uploadingFileProgressMap.remove(filePath)
    }
}