package com.nxg.im.call.component

import android.Manifest
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.blankj.utilcode.util.Utils
import com.nxg.commonui.utils.hideNavigationBars
import com.nxg.commonui.utils.hideSystemBars
import com.nxg.commonui.utils.setDecorFitsSystemWindows
import com.nxg.commonui.utils.showStatusBars
import com.nxg.im.call.component.databinding.ImCallFragmentVideoCallBinding
import com.nxg.im.core.IMClient
import com.nxg.im.core.data.db.KtChatDatabase
import com.nxg.im.core.module.state.VideoCallState
import com.nxg.im.core.module.state.VideoCallStateMachine
import com.nxg.mvvm.ktx.findMainActivityNavController
import com.nxg.mvvm.ktx.viewBinding
import com.nxg.mvvm.logger.SimpleLogger
import com.nxg.mvvm.ui.BaseBusinessFragment
import com.nxg.webrtcmobile.databinding.VideoCallFragmentBinding
import com.nxg.webrtcmobile.srs.SrsConstant
import com.nxg.webrtcmobile.webrtc.WebRtcHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.webrtc.EglBase
import org.webrtc.PeerConnectionFactory
import org.webrtc.RendererCommon

/**
 * 视频通话
 */
class VideoCallFragment : BaseBusinessFragment(R.layout.im_call_fragment_video_call), SimpleLogger {

    companion object {

        var PERMISSIONS_REQUIRED = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    private val binding by viewBinding(ImCallFragmentVideoCallBinding::bind)

    private val eglBase: EglBase by lazy {
        EglBase.create()
    }

    private var webRtcHelperLocal: WebRtcHelper? = null

    private var webRtcHelperRemote: WebRtcHelper? = null

    private val viewModel: VideoCallViewModel by activityViewModels()

    private val safeArgs: VideoCallFragmentArgs by navArgs()

    private var streamUrlLocal = "webrtc://${SrsConstant.SRS_SERVER_IP}/live/%s"

    private var streamUrlRemote = "webrtc://${SrsConstant.SRS_SERVER_IP}/live/%s"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logger.debug { "onViewCreated" }
        hideSystemBars()
        requireActivity().supportFragmentManager.addOnBackStackChangedListener(
            onBackStackChangedListener
        );
        //初始化SurfaceViewRenderer
        binding.renderLocal.apply {
            init(eglBase.eglBaseContext, object : RendererCommon.RendererEvents {
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
            init(eglBase.eglBaseContext, object : RendererCommon.RendererEvents {
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
        binding.btnHangup.setOnClickListener {
            if (VideoCallStateMachine.state is VideoCallState.CallOut) {
                logger.debug { "cancel: ${VideoCallStateMachine.state}" }
                IMClient.videoCallService.getSession(safeArgs.uuid)?.let { session ->
                    logger.debug { "cancel: $session" }
                    IMClient.videoCallService.cancel(session)
                }
            } else {
                logger.debug { "hangup: ${VideoCallStateMachine.state}" }
                IMClient.videoCallService.getSession(safeArgs.uuid)?.let { session ->
                    logger.debug { "hangup: $session" }
                    IMClient.videoCallService.hangup(session)
                }
            }
        }
        binding.btnAnswer.setOnClickListener {
            logger.debug { "answer: " }
            IMClient.videoCallService.getSession(safeArgs.uuid)?.let { session ->
                logger.debug { "answer: $session" }
                IMClient.videoCallService.answer(session)
            }
        }
        if (!checkSelfPermissions(requireContext())) {
            requestPermission(PERMISSIONS_REQUIRED)
        }
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                webRtcHelperLocal =
                    webRtcHelperLocal ?: WebRtcHelper.create(
                        requireContext(),
                        eglBase,
                        binding.renderLocal,
                        play = false
                    )
                webRtcHelperRemote = webRtcHelperRemote ?: WebRtcHelper.create(
                    requireContext(),
                    eglBase,
                    binding.renderRemote,
                    play = true
                )

                logger.debug { "getLoginData ${IMClient.authService.getLoginData()}" }
                IMClient.authService.getLoginData()?.let {
                    logger.debug {
                        "publish: ${
                            String.format(
                                streamUrlLocal,
                                it.user.uuid.toString()
                            )
                        } "
                    }
                    webRtcHelperLocal?.publish(
                        String.format(
                            streamUrlLocal,
                            it.user.uuid.toString()
                        )
                    )
                }
            }
        }
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                VideoCallStateMachine.stateFlow.collect {
                    logger.debug { "stateFlow collect VideoCallState -> $it" }
                    when (it) {
                        is VideoCallState.Connected -> {
                            binding.btnAnswer.visibility = View.GONE
                            lifecycleScope.launch(Dispatchers.IO) {
                                IMClient.authService.getLoginData()?.let { loginData ->
                                    logger.debug { "Connected：getSession by ${safeArgs.uuid}" }
                                    IMClient.videoCallService.getSession(safeArgs.uuid)
                                        ?.let { session ->
                                            logger.debug { "Connected：getFriend by $session" }
                                            session.signaling.participants.forEach { participant ->
                                                if (participant != loginData.user.uuid) {
                                                    KtChatDatabase.getInstance(Utils.getApp().applicationContext)
                                                        .friendDao()
                                                        .getFriend(
                                                            loginData.user.uuid,
                                                            participant
                                                        )?.let {
                                                            logger.debug {
                                                                "play: ${
                                                                    String.format(
                                                                        streamUrlRemote,
                                                                        participant.toString()
                                                                    )
                                                                } "
                                                            }
                                                            webRtcHelperRemote?.play(
                                                                String.format(
                                                                    streamUrlRemote,
                                                                    participant.toString()
                                                                )
                                                            )
                                                        }
                                                }
                                            }

                                        }

                                }
                            }

                        }

                        is VideoCallState.CallIn -> {
                            binding.btnHangup.visibility = View.VISIBLE
                            binding.btnAnswer.visibility = View.VISIBLE
                        }

                        is VideoCallState.CallOut -> {
                            binding.btnAnswer.visibility = View.GONE
                        }

                        VideoCallState.Idle -> {
                            //退出
                            findMainActivityNavController().navigateUp()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        logger.debug { "onDestroyView" }
        requireActivity().supportFragmentManager.removeOnBackStackChangedListener(
            onBackStackChangedListener
        );
        binding.renderRemote.release()
        binding.renderLocal.release()
        webRtcHelperLocal?.dispose()
        webRtcHelperRemote?.dispose()
        webRtcHelperLocal = null
        webRtcHelperRemote = null
        PeerConnectionFactory.stopInternalTracingCapture()
        PeerConnectionFactory.shutdownInternalTracer()
        super.onDestroyView()
        showStatusBars()
    }

    override fun doWhenPermissionNotGranted() {
        Toast.makeText(context, "权限被禁止，无法使用该功能！", Toast.LENGTH_LONG).show()
        findMainActivityNavController().navigateUp()
    }

    override fun doWhenPermissionGranted() {
        logger.debug { "doWhenPermissionGranted" }
    }

    private val onBackStackChangedListener = FragmentManager.OnBackStackChangedListener { logger.debug { "onBackStackChanged" } }


}