package com.nxg.rtmp_mobile

class RtmpMobile {

    companion object {
        // Used to load the 'rtmp_mobile' library on application startup.
        init {
            System.loadLibrary("rtmp_mobile")
        }
    }

    /**
     * 返回RTMP的版本号
     * @return
     */
    external fun nativeGetRtmpVersion(): String

    /**
     * 初始化
     * @param url 推流地址，如：rtmp://127.0.0.1:1935/live/livestream
     * @param w 视频流宽
     * @param h 视频流高
     * @param timeOut 超时
     * @return 返回RtmpClient对象指针
     */
    external fun nativeInit(url: String?, w: Int, h: Int, timeOut: Int): Long

    external fun nativeRelease(rtmpPtr: Long): Int

    external fun nativeSendSpsAndPps(
        rtmpPtr: Long,
        sps: ByteArray?,
        spsLen: Int,
        pps: ByteArray?,
        ppsLen: Int,
        timestamp: Long
    ): Int

    external fun nativeSendVideoData(
        rtmpPtr: Long,
        data: ByteArray?,
        len: Int,
        timestamp: Long
    ): Int

    external fun nativeSendAudioSpec(rtmpPtr: Long, data: ByteArray?, len: Int): Int

    external fun nativeSendAudioData(
        rtmpPtr: Long,
        data: ByteArray?,
        len: Int,
        timestamp: Long
    ): Int
}