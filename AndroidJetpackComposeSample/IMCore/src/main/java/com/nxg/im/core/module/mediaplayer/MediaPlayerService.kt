package com.nxg.im.core.module.mediaplayer

import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import androidx.annotation.RawRes
import com.blankj.utilcode.util.Utils
import com.nxg.im.core.IMService


interface MediaPlayerService : IMService {

    fun play(@RawRes resId: Int, isLooping: Boolean = false)

    fun stop()
}

object MediaPlayerServiceImpl : MediaPlayerService {

    private var mediaPlayer: MediaPlayer? = null
    private var afd: AssetFileDescriptor? = null

    override fun play(@RawRes resId: Int, isLooping: Boolean) {
        mediaPlayer = mediaPlayer ?: MediaPlayer()
        mediaPlayer?.apply {
            try {
                reset()
                afd = Utils.getApp().resources.openRawResourceFd(resId)
                afd?.let {
                    this.setDataSource(it)
                }
                this.isLooping = isLooping
                prepare()
                start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun stop() {
        mediaPlayer?.pause()
        mediaPlayer?.stop()
        afd?.close()
    }
}