package com.nxg.ffmpegstudy.component.h264

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import com.nxg.opencv.views.RenderSurfaceView
import com.nxg.audiorecord.AudioTrackHandler
import com.nxg.commonui.theme.AndroidJetpackComposeSampleTheme
import com.nxg.commonui.theme.ColorPrimary
import com.nxg.commonui.theme.ColorWarn
import com.nxg.ffmpeg_mobile.FFmpegMobile
import com.nxg.ffmpegstudy.component.R
import com.nxg.ffmpegstudy.component.databinding.AvH264DecodeStudyFragmentBinding
import com.nxg.mvvm.logger.SimpleLogger
import com.nxg.mvvm.ui.BaseViewModelFragment
import kotlinx.coroutines.InternalCoroutinesApi
import java.io.File

/**
 * h264解码学习
 */
class H264DecodeStudyComposeFragment : BaseViewModelFragment(), SimpleLogger {

    private lateinit var viewModel: H264DecodeStudyViewModel

    private var renderSurfaceView: RenderSurfaceView? = null
    private val audioTrackHandler by lazy {
        AudioTrackHandler.Builder().build()
    }
    private val decoderPtr by lazy {
        FFmpegMobile.nativeInit()
    }
    private var decodeVideoThread: DecodeVideoThread? = null
    private var decodeAudioThread: DecodeAudioThread? = null
    private lateinit var fileH264: File
    private lateinit var fileAAC: File
    private lateinit var videoOutputFilePath: String
    private lateinit var audioOutputFilePath: String

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AndroidJetpackComposeSampleTheme {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { Text(stringResource(id = R.string.av_h264_decode)) },
                            )
                        }
                    ) {
                        MainCompose(::start, ::stop)
                    }

                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        testFFmpegDecodeH264AndPlay()
    }

    @Preview()
    @Composable
    fun MainCompose(start: () -> Unit = {}, stop: () -> Unit = {}) {
        Column(
            modifier = Modifier
        ) {
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                factory = { context ->
                    RenderSurfaceView(context).also {
                        renderSurfaceView = it
                    }
                },
                update = { view ->

                }
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    modifier = Modifier
                        .padding(10.dp),
                    onClick = { start() },
                    contentPadding = PaddingValues(
                        start = 10.dp,
                        top = 10.dp,
                        end = 10.dp,
                        bottom = 10.dp
                    ), colors = ButtonDefaults.buttonColors(
                        backgroundColor = ColorPrimary.Primary,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "播放")
                }

                Button(
                    modifier = Modifier
                        .padding(10.dp),
                    onClick = { stop() },
                    contentPadding = PaddingValues(
                        start = 10.dp,
                        top = 10.dp,
                        end = 10.dp,
                        bottom = 10.dp
                    ), colors = ButtonDefaults.buttonColors(
                        backgroundColor = ColorWarn.Primary,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "停止")
                }
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun testFFmpegDecodeH264AndPlay() {
        val externalStorageDirectory = Environment.getExternalStorageDirectory().absolutePath
        val h264FilePath = "$externalStorageDirectory/test.h264"
        val aacFilePath = "$externalStorageDirectory/source.aac"
        fileH264 = File(h264FilePath)
        fileAAC = File(aacFilePath)
        logger.debug { "fileH264 ${fileH264.exists()}" }
        logger.debug { "fileAAC ${fileAAC.exists()}" }
        if (!fileH264.exists() && !fileAAC.exists()) {
            logger.debug { "both h264 aac not exists!" }
            return
        }
        videoOutputFilePath = "$externalStorageDirectory/test_output.h264"
        audioOutputFilePath = "$externalStorageDirectory/test_output_audio"
        audioTrackHandler.prepare()
        audioTrackHandler.start()
        audioTrackHandler.play()
    }

    override fun onStop() {
        super.onStop()
        stop()
    }

    private fun start(): Boolean {
        if (!fileH264.exists() && !fileAAC.exists()) {
            logger.debug { "both h264 aac not exists!" }
            return true
        }
        decodeAudioThread = DecodeAudioThread(
            "FFmpegDecodeAudioThread",
            decoderPtr,
            fileAAC.absolutePath,
            audioOutputFilePath,
            audioTrackHandler
        )
        renderSurfaceView?.let {
            decodeVideoThread = DecodeVideoThread(
                "FFmpegDecodeVideoThread",
                decoderPtr,
                fileH264.absolutePath,
                videoOutputFilePath,
                it
            )
        }
        decodeAudioThread?.start()
        decodeVideoThread?.start()
        return false
    }

    private fun stop() {
        audioTrackHandler.stop()
        audioTrackHandler.flush()
        renderSurfaceView?.release()
        decodeVideoThread?.interrupt()
        decodeAudioThread?.interrupt()
    }

}