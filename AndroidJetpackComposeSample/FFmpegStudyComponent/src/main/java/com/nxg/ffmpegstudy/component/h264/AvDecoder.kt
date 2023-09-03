package com.nxg.ffmpegstudy.component.h264

import android.util.Log
import com.nxg.opencv.views.RenderSurfaceView
import com.nxg.audiorecord.AudioTrackHandler
import com.nxg.ffmpeg_mobile.FFmpegMobile


class DecodeVideoThread(
    threadName: String,
    private val decoderPtr: Long,
    private val inputFilePath: String,
    private val outputFilePath: String,
    private val renderSurfaceView: RenderSurfaceView
) : Thread(threadName) {

    override fun run() {
        FFmpegMobile.nativeDecodeVideo(
            decoderPtr,
            inputFilePath,
            outputFilePath
        ) { width, height, data ->
            Log.i("DecodeVideoThread", "run: isInterrupted $isInterrupted")
            if (!isInterrupted) {
                if (data != null) {
                    //Log.i(TAG, "DecodeVideoThread onBufferListener: width = $width, height = $height, yuvData size  = " + data.size)
                    renderSurfaceView.onYuvData(RenderSurfaceView.YuvData(width, height, data))
                }
            }
        }
    }
}

class DecodeAudioThread(
    threadName: String,
    private val decoderPtr: Long,
    private val inputFilePath: String,
    private val outputFilePath: String,
    private val audioTrackHandler: AudioTrackHandler,
) : Thread(threadName) {

    override fun run() {
        FFmpegMobile.nativeDecodeAudio(
            decoderPtr,
            inputFilePath,
            outputFilePath
        ) { width, height, data ->
            Log.i("DecodeAudioThread", "run: isInterrupted $isInterrupted")
            if (!isInterrupted) {
                if (data != null) {
                    Log.i(
                        "DecodeAudioThread",
                        "DecodeAudioThread onBufferListener: width = $width, height = $height, pcmData = " + data.size
                    )
                    Log.i(
                        "DecodeAudioThread",
                        "DecodeVideoThread onPlaying: pcmData = $data"
                    )
                    audioTrackHandler.onPlaying(data, 0, data.size)
                }
            }
        }
    }
}