package com.nxg.composeplane.util

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool

/**
 * Android 多音频同时播放
 */
class SoundPoolUtil(private val context: Context) {
    private var audioManager: AudioManager? = null
    private var spool: SoundPool? = null
    private val steamType = AudioManager.STREAM_MUSIC
    private var mVolume = 0.5f//音量 0-1
    private var maxStreams = 10//同时播放的流的最大数量
    private var streamMaps = mutableMapOf<Any, Int>()

    companion object {
        const val TAG = "SoundPoolUtil"

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: SoundPoolUtil? = null

        fun getInstance(context: Context): SoundPoolUtil {
            return instance ?: synchronized(this) {
                instance ?: SoundPoolUtil(context).also { instance = it }
            }
        }

    }

    init {
        val attribute = AudioAttributes.Builder()
            .setLegacyStreamType(steamType)
            .build()
        spool = SoundPool.Builder()
            .setMaxStreams(maxStreams)
            .setAudioAttributes(attribute)
            .build()

        //设置加载文件监听
        spool?.setOnLoadCompleteListener { _: SoundPool, sampleId: Int, status: Int ->
            //首次异步加载完成播放
            if (status == 0) {
                //play(sampleId)
            }
            LogUtil.printLog(message = "setOnLoadCompleteListener status $status sampleId $sampleId")
        }

        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager?.let {
            val current = it.getStreamVolume(steamType).toFloat()
            val max = it.getStreamMaxVolume(steamType).toFloat()
            mVolume = current / max
        }

    }

    private fun play(
        streamId: Int,
        leftVolume: Float = mVolume,
        rightVolume: Float = mVolume,
        priority: Int = 1,
        loop: Int = 0,
        rate: Float = 1f
    ) {
        spool?.play(
            streamId, leftVolume //左音量0-1
            , rightVolume //右音量0-1
            , priority //流的优先级，值越大优先级高, 可单独设置
            , loop //循环播放的次数，0为值播放一次，-1为无限循环，其他值为播放loop+1次（例如，3为一共播放4次）, 可单独设置
            , rate
        ) //播放的速率，范围0.5-2.0(0.5为一半速率，1.0为正常速率，2.0为两倍速率), 可单独设置
    }

    fun setVolume(volume: Float) {
        mVolume = volume
    }

    fun getVolume(): Float {
        return mVolume
    }

    /**
     * 先load，之后再播放，防止load之后马上play导致无声音
     */
    fun load(soundResId: Int) {
        if (!streamMaps.contains(soundResId)) {
            // 从资源加载音频流
            spool?.let {
                streamMaps[soundResId] = it.load(context, soundResId, 1)
            }

        }
    }

    /**
     * 根据资源ID播放
     */
    fun playByRes(
        soundResId: Int,
        leftVolume: Float = mVolume,
        rightVolume: Float = mVolume,
        priority: Int = 1,
        loop: Int = 0,
        rate: Float = 1f
    ) {
        val streamId = (if (streamMaps.contains(soundResId)) {
            streamMaps[soundResId]
        } else {
            // 从资源截入音频流
            spool?.let {
                val stream = it.load(context, soundResId, 1)//priority暂时无效，建议设置为1
                streamMaps[soundResId] = stream
                null
            }

        }) ?: return

        play(streamId, leftVolume, rightVolume, priority, loop, rate)
    }


    /**
     * 根据文件地址播放
     */
    fun playByPath(path: String) {
        val streamId = (if (streamMaps.contains(path)) {
            streamMaps[path]
        } else {
            //从文件截入音频流
            spool?.let {
                val stream = it.load(path, 1)//priority暂时无效，建议设置为1
                streamMaps[path] = stream
                null
            }
        }) ?: return

        play(streamId)
    }

    fun pause() {
        //暂停所有音频
        spool?.autoPause()
    }

    fun resume() {
        //开始所有音频
        spool?.autoResume()
    }

    fun release() {
        //释放所有音频
        spool?.release()
    }


}
