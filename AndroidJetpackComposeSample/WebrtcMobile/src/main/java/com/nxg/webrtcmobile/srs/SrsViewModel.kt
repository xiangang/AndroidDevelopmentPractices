//package com.nxg.webrtcmobile.srs
//
//import android.content.Context
//import android.media.MediaCodecInfo
//import android.text.TextUtils
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.viewModelScope
//import androidx.lifecycle.viewmodel.initializer
//import androidx.lifecycle.viewmodel.viewModelFactory
//import com.blankj.utilcode.util.GsonUtils
//import com.nxg.mvvm.ktx.launchExceptionHandler
//import com.nxg.mvvm.logger.SimpleLogger
//import kotlinx.coroutines.Dispatchers
//import okhttp3.MediaType.Companion.toMediaType
//import okhttp3.RequestBody.Companion.toRequestBody
//import org.webrtc.*
//import org.webrtc.PeerConnection.RTCConfiguration
//import org.webrtc.PeerConnectionFactory.InitializationOptions
//import org.webrtc.audio.JavaAudioDeviceModule
//import org.webrtc.voiceengine.WebRtcAudioUtils
//import java.util.concurrent.atomic.AtomicBoolean
//
///**
// * SRS ViewModel
// */
//class SrsViewModel private constructor() : ViewModel(),
//    PeerConnection.Observer,
//    SdpObserver,
//    SimpleLogger {
//
//    private lateinit var peerConnectionFactory: PeerConnectionFactory
//    private lateinit var peerConnection: PeerConnection
//    private lateinit var videoSink: VideoSink
//    private var cameraVideoCapturer: VideoCapturer? = null
//    private lateinit var audioSource: AudioSource
//    private lateinit var videoSource: VideoSource
//    private lateinit var audioTrack: AudioTrack
//    private lateinit var videoTrack: VideoTrack
//    private lateinit var surfaceTextureHelper: SurfaceTextureHelper
//    private var play: AtomicBoolean = AtomicBoolean(false)
//    private var streamurl: String = ""
//
//
//    companion object {
//        private const val MEDIA_TYPE_JSON = "application/json;charset=utf-8"
//        private const val AUDIO_ECHO_CANCELLATION_CONSTRAINT = "googEchoCancellation"
//        private const val AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT = "googAutoGainControl"
//        private const val AUDIO_HIGH_PASS_FILTER_CONSTRAINT = "googHighpassFilter"
//        private const val AUDIO_NOISE_SUPPRESSION_CONSTRAINT = "googNoiseSuppression"
//        private const val VIDEO_TRACK_ID = "ARDAMSv0"
//        private const val AUDIO_TRACK_ID = "ARDAMSa0"
//        private const val VIDEO_RESOLUTION_WIDTH = 1280
//        private const val VIDEO_RESOLUTION_HEIGHT = 720
//        private const val FPS = 30
//
//        val Factory: ViewModelProvider.Factory = viewModelFactory {
//            initializer {
//                SrsViewModel()
//            }
//        }
//
//        /**
//         * 创建WebRtcHelper
//         */
//        fun create(
//            context: Context,
//            eglBase: EglBase,
//            surfaceViewRenderer: SurfaceViewRenderer
//        ): SrsViewModel {
//            return SrsViewModel().apply {
//                Logging.enableLogToDebugOutput(Logging.Severity.LS_NONE)//开启Debug日志输出
//                //创建端连接工厂
//                peerConnectionFactory = createPeerConnectionFactory(context, eglBase).also {
//                    //创建端连接
//                    peerConnection = it.createPeerConnection(getConfig(), this)!!
//                }
//
//                /*//设置仅接收音视频
//                peerConnection.addTransceiver(
//                    MediaStreamTrack.MediaType.MEDIA_TYPE_AUDIO,
//                    RtpTransceiverInit(RtpTransceiver.RtpTransceiverDirection.RECV_ONLY)
//                )
//                peerConnection.addTransceiver(
//                    MediaStreamTrack.MediaType.MEDIA_TYPE_VIDEO,
//                    RtpTransceiverInit(RtpTransceiver.RtpTransceiverDirection.RECV_ONLY)
//                )
//
//                //设置仅发送音视频
//                peerConnection.addTransceiver(
//                    MediaStreamTrack.MediaType.MEDIA_TYPE_AUDIO,
//                    RtpTransceiverInit(RtpTransceiver.RtpTransceiverDirection.SEND_ONLY)
//                )
//                peerConnection.addTransceiver(
//                    MediaStreamTrack.MediaType.MEDIA_TYPE_VIDEO,
//                    RtpTransceiverInit(RtpTransceiver.RtpTransceiverDirection.SEND_ONLY)
//                )*/
//
//                //设置回声消除
//                WebRtcAudioUtils.setWebRtcBasedAcousticEchoCanceler(true)
//                WebRtcAudioUtils.setWebRtcBasedNoiseSuppressor(true)
//                //音频Source
//                audioSource = peerConnectionFactory.createAudioSource(createAudioConstraints())
//                //创建音频轨
//                audioTrack = peerConnectionFactory.createAudioTrack(
//                    AUDIO_TRACK_ID,
//                    audioSource
//                )
//                audioTrack.setEnabled(true)
//                //添加音频轨
//                peerConnection.addTrack(audioTrack)
//
//                //创建视频渲染帮助类
//                surfaceTextureHelper =
//                    SurfaceTextureHelper.create("RTCCapture", eglBase.eglBaseContext)
//                //配置视频数据来自surfaceViewRenderer
//                videoSink = surfaceViewRenderer
//                //创建视频Source
//                videoSource = peerConnectionFactory.createVideoSource(false)
//                //创建视频轨
//                videoTrack = peerConnectionFactory.createVideoTrack(
//                    VIDEO_TRACK_ID,
//                    videoSource
//                )
//                videoTrack.setEnabled(true)
//                //创建视频捕获器
//                cameraVideoCapturer = createVideoCapturer(context)?.apply {
//                    //初始化，capturerObserver观察到相机捕获的数据后渲染通过surfaceTextureHelper渲染
//                    initialize(
//                        surfaceTextureHelper,//相机捕获到的视频数据渲染到surfaceTextureHelper
//                        context,
//                        videoSource.capturerObserver//视频数据捕获观察者
//                    )
//                    //根据指定的宽高和帧率开始捕获视频
//                    startCapture(
//                        VIDEO_RESOLUTION_WIDTH,
//                        VIDEO_RESOLUTION_HEIGHT,
//                        FPS
//                    )
//                }
//                //添加videoSink（surfaceViewRenderer）到视频轨，用于WebRTC推推流
//                videoTrack.addSink(videoSink)
//                //添加视频轨
//                peerConnection.addTrack(videoTrack)
//            }
//        }
//
//        /**
//         * 创建PeerConnectionFactory
//         */
//        private fun createPeerConnectionFactory(
//            context: Context,
//            eglBase: EglBase
//        ): PeerConnectionFactory {
//            //1.创建编码工厂
//            val encoderFactory: VideoEncoderFactory = DefaultVideoEncoderFactory(
//                eglBase.eglBaseContext,
//                false,
//                true
//            )
//            val encoderFactoryH264: DefaultVideoEncoderFactory =
//                createCustomVideoEncoderFactory(eglContext = eglBase.eglBaseContext,
//                    enableIntelVp8Encoder = true,
//                    enableH264HighProfile = true,
//                    videoEncoderSupportedCallback = object : VideoEncoderSupportedCallback {
//                        override fun isSupportedH264(info: MediaCodecInfo): Boolean {
//                            return true
//                        }
//
//                        override fun isSupportedVp8(info: MediaCodecInfo): Boolean {
//                            return false
//                        }
//
//                        override fun isSupportedVp9(info: MediaCodecInfo): Boolean {
//                            return false
//                        }
//                    })
//            //2.创建解码工厂
//            val decoderFactory: VideoDecoderFactory =
//                DefaultVideoDecoderFactory(eglBase.eglBaseContext)
//
//            //3.PeerConnectionFactory初始化initialize
//            val initializationOptions = InitializationOptions.builder(context)
//                .setEnableInternalTracer(true)
//                .setFieldTrials("WebRTC-H264HighProfile/Enabled/")
//                .createInitializationOptions()
//            PeerConnectionFactory.initialize(initializationOptions)
//            //4.创建PeerConnectionFactory
//            return PeerConnectionFactory.builder()
//                .setOptions(PeerConnectionFactory.Options())
//                .setAudioDeviceModule(
//                    JavaAudioDeviceModule.builder(context).createAudioDeviceModule()
//                )
//                .setVideoEncoderFactory(encoderFactoryH264)
//                .setVideoDecoderFactory(decoderFactory)
//                .createPeerConnectionFactory()
//        }
//
//        private fun getConfig(): RTCConfiguration? {
//            val rtcConfig = RTCConfiguration(ArrayList())
//            //关闭分辨率变换
//            rtcConfig.enableCpuOveruseDetection = false
//            //修改模式PlanB无法使用仅接收音视频的配置
//            rtcConfig.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
//            return rtcConfig
//        }
//
//
//        /**
//         * 配置音频参数
//         * @return
//         */
//        private fun createAudioConstraints(): MediaConstraints {
//            val audioConstraints = MediaConstraints()
//            audioConstraints.mandatory.add(
//                MediaConstraints.KeyValuePair(
//                    AUDIO_ECHO_CANCELLATION_CONSTRAINT,
//                    "true"
//                )
//            )
//            audioConstraints.mandatory.add(
//                MediaConstraints.KeyValuePair(
//                    AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT,
//                    "false"
//                )
//            )
//            audioConstraints.mandatory.add(
//                MediaConstraints.KeyValuePair(
//                    AUDIO_HIGH_PASS_FILTER_CONSTRAINT,
//                    "false"
//                )
//            )
//            audioConstraints.mandatory.add(
//                MediaConstraints.KeyValuePair(
//                    AUDIO_NOISE_SUPPRESSION_CONSTRAINT,
//                    "true"
//                )
//            )
//            return audioConstraints
//        }
//
//        /**
//         * 创建视频捕获器
//         *
//         * @return VideoCapturer
//         */
//        private fun createVideoCapturer(context: Context): VideoCapturer? {
//            return if (Camera2Enumerator.isSupported(context)) {
//                createCameraCapturer(Camera2Enumerator(context))
//            } else {
//                createCameraCapturer(Camera1Enumerator(true))
//            }
//        }
//
//        /**
//         * 创建相机媒体流
//         */
//        private fun createCameraCapturer(enumerator: CameraEnumerator): VideoCapturer? {
//            val deviceNames = enumerator.deviceNames
//            // First, try to find front facing camera
//            for (deviceName in deviceNames) {
//                if (enumerator.isFrontFacing(deviceName)) {
//                    val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
//                    if (videoCapturer != null) {
//                        return videoCapturer
//                    }
//                }
//            }
//            // Front facing camera not found, try something else
//            for (deviceName in deviceNames) {
//                if (!enumerator.isFrontFacing(deviceName)) {
//                    val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
//                    if (videoCapturer != null) {
//                        return videoCapturer
//                    }
//                }
//            }
//            return null
//        }
//    }
//
//    /**
//     * 释放资源
//     */
//    fun dispose() {
//        peerConnection.dispose()
//        surfaceTextureHelper.dispose()
//        cameraVideoCapturer?.dispose()
//        peerConnectionFactory.dispose()
//    }
//
//    /**
//     * 推流
//     */
//    fun publish(streamUrl: String) {
//        play.set(false)
//        createOffer(streamUrl)
//    }
//
//    /**
//     * 拉流
//     */
//    fun play(streamUrl: String) {
//        play.set(true)
//        createOffer(streamUrl)
//    }
//
//    /**
//     * 创建Offer开始WebRTC推拉流
//     */
//    private fun createOffer(streamUrl: String) {
//        this.streamurl = streamUrl
//        //创建Offer，成功后onCreateSuccess回调开始播放
//        peerConnection.createOffer(this, MediaConstraints())
//    }
//
//    private fun doRequest(sdp: String) {
//        logger.debug { "doRequest: $sdp, $streamurl, ${play.get()}" }
//        val srsRequestBean = SrsRequestBean()
//        srsRequestBean.streamurl = this.streamurl
//        srsRequestBean.sdp = sdp
//        launchExceptionHandler(viewModelScope, Dispatchers.Default, {
//            val mediaType = MEDIA_TYPE_JSON.toMediaType()
//            val requestBody = GsonUtils.toJson(srsRequestBean).toRequestBody(mediaType)
//            val srsResponseBean = if (play.get()) {
//                SrsHttpManger.srsApiService.play(requestBody)
//            } else {
//                SrsHttpManger.srsApiService.publish(requestBody)
//            }
//            logger.debug { "doRequest: $srsResponseBean" }
//            if (!srsResponseBean.sdp.isNullOrEmpty()) {
//                val remoteSdp = SessionDescription(SessionDescription.Type.ANSWER, sdp)
//                peerConnection.setRemoteDescription(this@SrsViewModel, remoteSdp)
//            }
//        }, onError = {
//            logger.error {
//                "onError:${it.message}"
//            }
//        }, onComplete = {
//            logger.debug { "onComplete" }
//        })
//    }
//
//
//    //SdpObserver
//    override fun onCreateSuccess(sdp: SessionDescription) {
//        logger.debug { "onCreateSuccess:$sdp" }
//        if (sdp.type == SessionDescription.Type.OFFER) {
//            //设置setLocalDescription offer返回sdp
//            peerConnection.setLocalDescription(this, sdp)
//            if (!TextUtils.isEmpty(sdp.description)) {
//                doRequest(sdp.description)
//            }
//        }
//    }
//
//    override fun onSetSuccess() {
//        logger.debug { "onSetSuccess:" }
//    }
//
//    override fun onCreateFailure(p0: String?) {
//        logger.debug { "onCreateFailure:$p0" }
//    }
//
//    override fun onSetFailure(p0: String?) {
//        logger.debug { "onSetFailure:$p0" }
//    }
//
//
//    //PeerConnection.Observer
//    override fun onSignalingChange(p0: PeerConnection.SignalingState?) {
//        logger.debug { "onSignalingChange: $p0" }
//    }
//
//    override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState?) {
//        logger.debug { "onIceConnectionChange: $p0" }
//    }
//
//    override fun onIceConnectionReceivingChange(p0: Boolean) {
//        logger.debug { "onIceConnectionReceivingChange: $p0" }
//    }
//
//    override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {
//        logger.debug { "onIceGatheringChange: $p0" }
//    }
//
//    override fun onIceCandidate(iceCandidate: IceCandidate?) {
//        logger.debug { "onIceGatheringChange: $iceCandidate" }
//        peerConnection.addIceCandidate(iceCandidate)
//    }
//
//    override fun onIceCandidatesRemoved(candidates: Array<out IceCandidate>?) {
//        logger.debug { "onIceGatheringChange: $candidates" }
//        peerConnection.removeIceCandidates(candidates)
//    }
//
//    override fun onAddStream(p0: MediaStream?) {
//        logger.debug { "onAddStream: $p0" }
//    }
//
//    override fun onRemoveStream(p0: MediaStream?) {
//        logger.debug { "onRemoveStream: $p0" }
//    }
//
//    override fun onDataChannel(p0: DataChannel?) {
//        logger.debug { "onDataChannel: $p0" }
//    }
//
//    override fun onRenegotiationNeeded() {
//        logger.debug { "onRenegotiationNeeded:" }
//    }
//
//    override fun onAddTrack(receiver: RtpReceiver, mediaStreams: Array<out MediaStream>) {
//        logger.debug { "onAddTrack: $receiver, $mediaStreams" }
//        //如果是play，则远程的媒体流轨添加videoSink(surfaceViewRenderer),进行画面渲染
//        val remoteMediaStreamTrack: MediaStreamTrack? = receiver.track()
//        if (remoteMediaStreamTrack is VideoTrack) {
//            remoteMediaStreamTrack.addSink(videoSink)
//        }
//    }
//
//}