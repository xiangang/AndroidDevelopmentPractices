package com.nxg.androidsample.main

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
            if (data != null) {
                renderSurfaceView.onYuvData(RenderSurfaceView.YuvData(width, height, data))
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
            if (data != null) {
                audioTrackHandler.onPlaying(data, 0, data.size)
            }
        }
    }
}