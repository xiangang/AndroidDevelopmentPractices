package com.nxg.im.core.utils

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import coil.decode.VideoFrameDecoder
import coil.size.Dimension
import coil.size.Scale
import coil.size.Size
import coil.size.isOriginal
import coil.size.pxOrElse
import com.blankj.utilcode.util.Utils

object VideoUtils {

    val videoFrameDecoder by lazy {
        VideoFrameDecoder(Utils.getApp())
    }

    /**
     * 获取视频缩略图
     */
    @JvmStatic
    fun getVideoThumbnail(context: Context, uri: Uri): Bitmap? {
        val media = MediaMetadataRetriever()
        media.setDataSource(context, uri)
        return media.frameAtTime
    }

}
