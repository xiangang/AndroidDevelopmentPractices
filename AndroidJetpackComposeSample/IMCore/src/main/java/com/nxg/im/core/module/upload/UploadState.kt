package com.nxg.im.core.module.upload

/**
 * 上传状态机
 */
sealed class UploadState {
    /**
     * 未开始
     */
    object UnStart : UploadState()

    /**
     * 文件不存在
     */
    object FileNotExist : UploadState()

    /**
     * 上传完成
     */
    object Complete : UploadState()

    /**
     * 上传中
     */
    class Progress(var totalNum: Long, var current: Long) : UploadState()

    /**
     * 失败
     */
    class Error(val e: Exception) : UploadState()
}
