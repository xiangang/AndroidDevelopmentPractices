package com.nxg.ffmpegstudy.component.h264

import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.nxg.acodecmobile.views.RenderSurfaceView
import com.nxg.audiorecord.AudioTrackHandler
import com.nxg.audiorecord.LogUtil
import com.nxg.ffmpeg_mobile.FFmpegMobile
import com.nxg.ffmpegstudy.component.databinding.AvH264DecodeStudyFragmentBinding
import com.nxg.mvvm.logger.SimpleLogger
import java.io.File

/**
 * h264解码学习
 */
class H264DecodeStudyFragment : Fragment(), SimpleLogger {

    private lateinit var viewModel: H264DecodeStudyViewModel

    private var _binding: AvH264DecodeStudyFragmentBinding? = null

    private val binding get() = _binding!!

    private lateinit var renderSurfaceView: RenderSurfaceView
    private lateinit var surfaceHolder: SurfaceHolder
    private var decodeVideoThread: DecodeVideoThread? = null
    private var decodeAudioThread: DecodeAudioThread? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[H264DecodeStudyViewModel::class.java]
        _binding = AvH264DecodeStudyFragmentBinding.inflate(inflater, container, false)
        renderSurfaceView = binding.renderSurfaceView
        surfaceHolder = renderSurfaceView.holder
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        testFFmpegDecodeH264AndPlay()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun testFFmpegDecodeH264AndPlay() {
        val decoderPtr = FFmpegMobile.nativeInit()
        val externalStorageDirectory = Environment.getExternalStorageDirectory().absolutePath
        val h264FilePath = "$externalStorageDirectory/test.h264"
        val aacFilePath = "$externalStorageDirectory/source.aac"
        val fileH264 = File(h264FilePath)
        val fileAAC = File(aacFilePath)
        logger.debug { "fileH264 ${fileH264.exists()}" }
        logger.debug { "fileAAC ${fileAAC.exists()}" }
        Log.i("H264DecodeStudyFragment", "fileAAC ${fileAAC.exists()}")
        Log.i("H264DecodeStudyFragment", "fileH264 ${fileH264.exists()}")
        if (!fileH264.exists() && !fileAAC.exists()) {
            logger.debug { "both h264 aac not exists!" }
            return
        }
        val flvFilePath =
            Environment.getExternalStorageDirectory().absolutePath + "/source.200kbps.768x320.flv"
        val videoOutputFilePath =
            Environment.getExternalStorageDirectory().absolutePath + "/test_output.h264"
        val audioOutputFilePath =
            Environment.getExternalStorageDirectory().absolutePath + "/test_output_audio"
        val audioTrackHandler = AudioTrackHandler.Builder().build()
        audioTrackHandler.prepare()
        audioTrackHandler.start()
        audioTrackHandler.play()
        binding.buttonStart.setOnClickListener {
            if (!fileH264.exists() && !fileAAC.exists()) {
                logger.debug { "both h264 aac not exists!" }
                return@setOnClickListener
            }
            decodeAudioThread = DecodeAudioThread(
                "FFmpegDecodeAudioThread",
                decoderPtr,
                aacFilePath,
                audioOutputFilePath,
                audioTrackHandler
            )
            decodeVideoThread = DecodeVideoThread(
                "FFmpegDecodeVideoThread",
                decoderPtr,
                h264FilePath,
                videoOutputFilePath,
                renderSurfaceView
            )
            decodeAudioThread?.start()
            decodeVideoThread?.start()
        }

        binding.buttonStop.setOnClickListener {
            decodeVideoThread?.interrupt()
            decodeAudioThread?.interrupt()
        }
    }

}