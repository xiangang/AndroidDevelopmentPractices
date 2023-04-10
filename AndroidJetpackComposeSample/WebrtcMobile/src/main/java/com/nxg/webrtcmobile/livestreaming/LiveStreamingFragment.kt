package com.nxg.webrtcmobile.livestreaming

import android.Manifest
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.nxg.mvvm.ktx.findMainActivityNavController
import com.nxg.mvvm.ktx.viewBinding
import com.nxg.mvvm.logger.SimpleLogger
import com.nxg.mvvm.ui.BaseBusinessFragment
import com.nxg.webrtcmobile.R
import com.nxg.webrtcmobile.databinding.LiveStreamingFragmentBinding
import com.nxg.webrtcmobile.srs.SrsConstant
import com.nxg.webrtcmobile.webrtc.WebRtcHelper
import org.webrtc.EglBase
import org.webrtc.PeerConnectionFactory
import org.webrtc.RendererCommon
import org.webrtc.RendererCommon.RendererEvents

class LiveStreamingFragment : BaseBusinessFragment(R.layout.live_streaming_fragment), SimpleLogger {

    companion object {
        var PERMISSIONS_REQUIRED = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

    }

    private val binding by viewBinding(LiveStreamingFragmentBinding::bind)
    private val viewModel: LiveStreamingViewModel by viewModels()
    private val eglBase: EglBase by lazy {
        EglBase.create()
    }
    private var webRtcHelperLocal: WebRtcHelper? = null
    private var webRtcHelperRemote: WebRtcHelper? = null
    private var streamUrlLocal = "webrtc://${SrsConstant.SRS_SERVER_IP}/live/livestream2"
    private var streamUrlRemote = "webrtc://${SrsConstant.SRS_SERVER_IP}/live/livestream"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logger.debug { "onViewCreated" }
        //初始化SurfaceViewRenderer
        binding.renderLocal.apply {
            init(eglBase.eglBaseContext, object : RendererEvents {
                override fun onFirstFrameRendered() {}
                override fun onFrameResolutionChanged(
                    videoWidth: Int,
                    videoHeight: Int,
                    rotation: Int
                ) {
                }
            })
            setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL)
            setEnableHardwareScaler(true)
            setZOrderMediaOverlay(true)

        }
        binding.renderRemote.apply {
            init(eglBase.eglBaseContext, object : RendererEvents {
                override fun onFirstFrameRendered() {}
                override fun onFrameResolutionChanged(
                    videoWidth: Int,
                    videoHeight: Int,
                    rotation: Int
                ) {
                }
            })
            setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL)
            setEnableHardwareScaler(true)
            setZOrderMediaOverlay(true)
        }

        /*if (!checkSelfPermissions(requireContext())) {
            requestPermission(PERMISSIONS_REQUIRED)
        }*/

        lifecycleScope.launchWhenCreated {
            webRtcHelperLocal =
                webRtcHelperLocal ?: WebRtcHelper.create(
                    requireContext(),
                    eglBase,
                    binding.renderLocal,
                    play = false
                ).apply {
                    //替换为实际的流地址
                    logger.debug { "publish: $streamUrlLocal" }
                    publish(streamUrlLocal)
                }
            webRtcHelperRemote = webRtcHelperRemote ?: WebRtcHelper.create(
                requireContext(),
                eglBase,
                binding.renderRemote
            ).apply {
                //替换为实际的流地址
                logger.debug { "play: $streamUrlRemote" }
                play(streamUrlRemote)
            }

        }

    }

    override fun onDestroyView() {
        logger.debug { "onDestroyView" }
        binding.renderRemote.release()
        binding.renderLocal.release()
        webRtcHelperLocal?.dispose()
        webRtcHelperRemote?.dispose()
        webRtcHelperLocal = null
        webRtcHelperRemote = null
        PeerConnectionFactory.stopInternalTracingCapture()
        PeerConnectionFactory.shutdownInternalTracer()
        super.onDestroyView()

    }

    override fun doWhenPermissionNotGranted() {
        Toast.makeText(context, "权限被禁止，无法使用该功能！", Toast.LENGTH_LONG).show()
        findMainActivityNavController().navigateUp()
    }

    override fun doWhenPermissionGranted() {
        logger.debug { "doWhenPermissionGranted" }
    }

}