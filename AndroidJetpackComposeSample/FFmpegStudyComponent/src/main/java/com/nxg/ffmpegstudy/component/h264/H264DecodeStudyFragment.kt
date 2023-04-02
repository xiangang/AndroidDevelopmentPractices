package com.nxg.ffmpegstudy.component.h264

import android.os.Build
import android.os.Bundle
import android.os.Environment
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
import java.io.File

/**
 * h264解码学习
 */
class H264DecodeStudyFragment : Fragment() {

    companion object {
        const val TAG = "H264DecodeStudyFragment"
    }

    private lateinit var viewModel: H264DecodeStudyViewModel


    private var _binding: AvH264DecodeStudyFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
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
        renderSurfaceView = _binding!!.renderSurfaceView
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
        val h264FilePath = Environment.getExternalStorageDirectory().absolutePath + "/test.h264"
        val flvFilePath =
            Environment.getExternalStorageDirectory().absolutePath + "/source.200kbps.768x320.flv"
        val aacFilePath =
            Environment.getExternalStorageDirectory().absolutePath + "/source.aac"
        val file = File(h264FilePath)
        LogUtil.i(TAG, "file ${file.exists()}")
        val videoOutputFilePath =
            Environment.getExternalStorageDirectory().absolutePath + "/test_output.h264"

        val audioOutputFilePath =
            Environment.getExternalStorageDirectory().absolutePath + "/test_output_audio"
        val audioTrackHandler = AudioTrackHandler.Builder().build()
        audioTrackHandler.prepare()
        audioTrackHandler.start()
        audioTrackHandler.play()
        binding.buttonStart.setOnClickListener {
            /*Thread {
                FFmpegMobile.nativePlayAudio(aacFilePath)
            }.start()*/

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