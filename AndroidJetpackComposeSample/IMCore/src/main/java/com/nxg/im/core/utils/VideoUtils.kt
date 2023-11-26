package com.nxg.im.core.utils

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import com.blankj.utilcode.util.Utils
import java.io.IOException

object VideoUtils {

    val videoFrameDecoder by lazy {
        VideoFrameDecoder(Utils.getApp())
    }

    /**
     * 获取视频缩略图
     */
    @JvmStatic
    fun getVideoThumbnail(context: Context, uri: Uri): Bitmap? {
        val mediaMetadataRetriever = MediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(context, uri)
        return mediaMetadataRetriever.frameAtTime
    }


    /**
     * 获取视频时长,这里获取的是毫秒
     */
    @JvmStatic
    fun getVideoDuration(context: Context, uri: Uri): Int {
        try {
            val mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(context, uri)
            mediaPlayer.prepare()
            return mediaPlayer.duration
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return 0
    }

    /**
     * 获取视频时长,这里获取的是毫秒
     *
     * @return
     */
    @JvmStatic
    fun getVideoDuration(videoPath: String): Int {
        var duration = 0
        try {
            val mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(videoPath)
            duration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!.toInt()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return duration
    }

    @JvmStatic
    fun getVideoSizeBitrate(context: Context, uri: Uri): Pair<android.util.Size, Int> {
        val mediaMetadataRetriever = MediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(context, uri)
        val originWidth =
            mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toInt() ?: 0
        val originHeight =
            mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toInt()
                ?: 0
        val bitrate =
            mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)?.toInt() ?: 0
        return Pair(android.util.Size(originWidth, originHeight), bitrate)
    }

    /**
     * 将视频的时长duration转换成时分秒的格式显示：
     */
    @JvmStatic
    fun formatDuration(duration: Int): String {
        val hours = duration / 3600
        val minutes = (duration % 3600) / 60
        val seconds = duration % 60
        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds)
        }
        return String.format("%02d:%02d", minutes, seconds)
    }
}
