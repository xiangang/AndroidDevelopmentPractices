package com.nxg.webrtcmobile.webrtc

import android.content.Context
import android.media.MediaCodecInfo
import android.text.TextUtils
import com.blankj.utilcode.util.GsonUtils
import com.nxg.mvvm.ktx.launchExceptionHandler
import com.nxg.mvvm.logger.SimpleLogger
import com.nxg.webrtcmobile.srs.SrsConstant
import com.nxg.webrtcmobile.srs.SrsHttpManger
import com.nxg.webrtcmobile.srs.SrsRequestBean
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.webrtc.*
import org.webrtc.PeerConnection.RTCConfiguration
import org.webrtc.PeerConnectionFactory.InitializationOptions
import org.webrtc.audio.JavaAudioDeviceModule
import org.webrtc.voiceengine.WebRtcAudioUtils
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

//WebRTC调度器
val WebRTCDispatcher: CoroutineDispatcher = Executors.newSingleThreadExecutor { runnable ->
    Thread(runnable, "WebRTCDispatcher")
}.asCoroutineDispatcher()


//WebRTC作用域
val WebRTCScope: CoroutineScope = CoroutineScope(WebRTCDispatcher + SupervisorJob())

/**
 * SRS ViewModel
 */
class WebRtcHelper private constructor() :
    PeerConnection.Observer,
    SdpObserver,
    SimpleLogger {

    private lateinit var peerConnectionFactory: PeerConnectionFactory
    private lateinit var peerConnection: PeerConnection
    private lateinit var surfaceViewRenderer: SurfaceViewRenderer
    private var videoSink: VideoSink? = null
    private var cameraVideoCapturer: VideoCapturer? = null
    private var surfaceTextureHelper: SurfaceTextureHelper? = null
    private var play: AtomicBoolean = AtomicBoolean(false)
    private var streamUrl: String = ""


    companion object {
        private const val MEDIA_TYPE_JSON = "application/json;charset=utf-8"
        private const val AUDIO_ECHO_CANCELLATION_CONSTRAINT = "googEchoCancellation"
        private const val AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT = "googAutoGainControl"
        private const val AUDIO_HIGH_PASS_FILTER_CONSTRAINT = "googHighpassFilter"
        private const val AUDIO_NOISE_SUPPRESSION_CONSTRAINT = "googNoiseSuppression"
        private const val VIDEO_TRACK_ID = "ARDAMSv0"
        private const val AUDIO_TRACK_ID = "ARDAMSa0"
        private const val VIDEO_RESOLUTION_WIDTH = 1280
        private const val VIDEO_RESOLUTION_HEIGHT = 720
        private const val FPS = 30

        /**
         * 创建WebRtcHelper
         */
        fun create(
            context: Context,
            eglBase: EglBase,
            surfaceViewRenderer: SurfaceViewRenderer,
            play: Boolean = true
        ): WebRtcHelper {
            return WebRtcHelper().apply {
                this.play.set(play)
                this.surfaceViewRenderer = surfaceViewRenderer
                //加载并初始化WebRTC，在创建 PeerConnectionFactory之前必须至少调用一次
                PeerConnectionFactory.initialize(
                    InitializationOptions.builder(context)
                        .setEnableInternalTracer(true)
                        .setFieldTrials("WebRTC-H264HighProfile/Enabled/")
                        .createInitializationOptions()
                )

                //开启Debug日志输出
                Logging.enableLogToDebugOutput(Logging.Severity.LS_NONE)

                //创建端连接工厂
                peerConnectionFactory = createPeerConnectionFactory(context, eglBase).also {
                    //创建端连接
                    peerConnection = it.createPeerConnection(getConfig(), this)!!
                }

                //创建视频渲染帮助类
                surfaceTextureHelper =
                    SurfaceTextureHelper.create("SurfaceTextureHelper", eglBase.eglBaseContext)
                //配置视频数据来自surfaceViewRenderer
                videoSink = surfaceViewRenderer

                //播放模式
                if (this.play.get()) {
                    //设置仅接收音视频
                    peerConnection.addTransceiver(
                        MediaStreamTrack.MediaType.MEDIA_TYPE_AUDIO,
                        RtpTransceiver.RtpTransceiverInit(RtpTransceiver.RtpTransceiverDirection.RECV_ONLY)
                    )
                    peerConnection.addTransceiver(
                        MediaStreamTrack.MediaType.MEDIA_TYPE_VIDEO,
                        RtpTransceiver.RtpTransceiverInit(RtpTransceiver.RtpTransceiverDirection.RECV_ONLY)
                    )
                } else {
                    //设置仅发送音视频
                    peerConnection.addTransceiver(
                        MediaStreamTrack.MediaType.MEDIA_TYPE_AUDIO,
                        RtpTransceiver.RtpTransceiverInit(RtpTransceiver.RtpTransceiverDirection.SEND_ONLY)
                    )
                    peerConnection.addTransceiver(
                        MediaStreamTrack.MediaType.MEDIA_TYPE_VIDEO,
                        RtpTransceiver.RtpTransceiverInit(RtpTransceiver.RtpTransceiverDirection.SEND_ONLY)
                    )
                    //设置回声消除
                    WebRtcAudioUtils.setWebRtcBasedAcousticEchoCanceler(true)
                    WebRtcAudioUtils.setWebRtcBasedNoiseSuppressor(true)
                    //创建音频源
                    peerConnectionFactory.createAudioSource(createAudioConstraints())
                        .let { audioSource ->
                            //创建音频轨
                            peerConnectionFactory.createAudioTrack(
                                AUDIO_TRACK_ID,
                                audioSource
                            ).also { audioTrack ->
                                audioTrack.setEnabled(true)
                                //添加音频轨
                                peerConnection.addTrack(audioTrack)
                            }
                        }

                    //创建视频源
                    peerConnectionFactory.createVideoSource(false)
                        .also { videoSource ->
                            //创建视频轨
                            peerConnectionFactory.createVideoTrack(
                                VIDEO_TRACK_ID,
                                videoSource
                            ).also { videoTrack ->
                                videoTrack.setEnabled(true)
                                //创建视频捕获器
                                cameraVideoCapturer = createVideoCapturer(context)?.apply {
                                    //初始化，capturerObserver观察到相机捕获的数据后渲染通过surfaceTextureHelper渲染
                                    initialize(
                                        surfaceTextureHelper,//相机捕获到的视频数据渲染到surfaceTextureHelper
                                        context,
                                        videoSource.capturerObserver//视频数据捕获观察者
                                    )
                                    //根据指定的宽高和帧率开始捕获视频
                                    startCapture(
                                        VIDEO_RESOLUTION_WIDTH,
                                        VIDEO_RESOLUTION_HEIGHT,
                                        FPS
                                    )
                                }
                                //添加videoSink（surfaceViewRenderer）到视频轨，用于WebRTC推推流
                                videoTrack.addSink(videoSink)
                                //添加视频轨
                                peerConnection.addTrack(videoTrack)
                            }
                        }
                }
            }
        }

        /**
         * 创建PeerConnectionFactory
         */
        private fun createPeerConnectionFactory(
            context: Context,
            eglBase: EglBase
        ): PeerConnectionFactory {
            //1.创建编码工厂
            val encoderFactoryH264: DefaultVideoEncoderFactory =
                createCustomVideoEncoderFactory(eglContext = eglBase.eglBaseContext,
                    enableIntelVp8Encoder = true,
                    enableH264HighProfile = true,
                    videoEncoderSupportedCallback = object : VideoEncoderSupportedCallback {
                        override fun isSupportedH264(info: MediaCodecInfo): Boolean {
                            //这里自行添加支持H.264编码的MediaCodecInfo
                            return true
                        }

                        override fun isSupportedVp8(info: MediaCodecInfo): Boolean {
                            return false
                        }

                        override fun isSupportedVp9(info: MediaCodecInfo): Boolean {
                            return false
                        }
                    })
            //2.创建解码工厂
            val decoderFactory: VideoDecoderFactory =
                DefaultVideoDecoderFactory(eglBase.eglBaseContext)

            //3.创建PeerConnectionFactory
            return PeerConnectionFactory.builder()
                .setOptions(PeerConnectionFactory.Options())
                .setAudioDeviceModule(
                    JavaAudioDeviceModule.builder(context).createAudioDeviceModule()
                )
                .setVideoEncoderFactory(encoderFactoryH264)
                .setVideoDecoderFactory(decoderFactory)
                .createPeerConnectionFactory()
        }

        private fun getConfig(): RTCConfiguration {
            val rtcConfig = RTCConfiguration(ArrayList())
            //关闭分辨率变换
            rtcConfig.enableCpuOveruseDetection = false
            //修改模式PlanB无法使用仅接收音视频的配置
            rtcConfig.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
            return rtcConfig
        }


        /**
         * 配置音频参数
         * @return
         */
        private fun createAudioConstraints(): MediaConstraints {
            val audioConstraints = MediaConstraints()
            //回声消
            audioConstraints.mandatory.add(
                MediaConstraints.KeyValuePair(
                    AUDIO_ECHO_CANCELLATION_CONSTRAINT,
                    "true"
                )
            )
            //自动增益
            audioConstraints.mandatory.add(
                MediaConstraints.KeyValuePair(
                    AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT,
                    "false"
                )
            )
            //高音过滤
            audioConstraints.mandatory.add(
                MediaConstraints.KeyValuePair(
                    AUDIO_HIGH_PASS_FILTER_CONSTRAINT,
                    "false"
                )
            )
            //噪音处理
            audioConstraints.mandatory.add(
                MediaConstraints.KeyValuePair(
                    AUDIO_NOISE_SUPPRESSION_CONSTRAINT,
                    "true"
                )
            )
            return audioConstraints
        }

        /**
         * 创建视频捕获器
         *
         * @return VideoCapturer
         */
        private fun createVideoCapturer(context: Context): VideoCapturer? {
            return if (Camera2Enumerator.isSupported(context)) {
                createVideoCapturerFromCamera(Camera2Enumerator(context))
            } else {
                createVideoCapturerFromCamera(Camera1Enumerator(true))
            }
        }

        /**
         * 通过相机创建视频捕获器
         */
        private fun createVideoCapturerFromCamera(enumerator: CameraEnumerator): VideoCapturer? {
            val deviceNames = enumerator.deviceNames
            // First, try to find front facing camera
            for (deviceName in deviceNames) {
                if (enumerator.isFrontFacing(deviceName)) {
                    val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                    if (videoCapturer != null) {
                        return videoCapturer
                    }
                }
            }
            // Front facing camera not found, try something else
            for (deviceName in deviceNames) {
                if (!enumerator.isFrontFacing(deviceName)) {
                    val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                    if (videoCapturer != null) {
                        return videoCapturer
                    }
                }
            }
            return null
        }
    }

    /**
     * 释放资源
     */
    fun dispose() {
        cameraVideoCapturer?.dispose()
        surfaceViewRenderer.clearImage()
        surfaceTextureHelper?.dispose()
        peerConnection.dispose()
        peerConnectionFactory.dispose()
    }

    /**
     * 推流
     */
    fun publish(streamUrl: String) {
        logger.debug { "publish: streamUrl $streamUrl" }
        if (play.get()) {
            logger.error { "Support play only!" }
            return
        }
        createOffer(streamUrl)
    }

    /**
     * 拉流
     */
    fun play(streamUrl: String) {
        logger.debug { "play: streamUrl $streamUrl" }
        if (!play.get()) {
            logger.error { "Support publish only!" }
            return
        }
        createOffer(streamUrl)
    }

    /**
     * 创建Offer开始WebRTC推拉流
     */
    private fun createOffer(streamUrl: String) {
        logger.debug { "createOffer: $streamUrl" }
        this.streamUrl = streamUrl
        //创建Offer，成功后onCreateSuccess回调开始播放
        peerConnection.createOffer(this, MediaConstraints())
    }

    private fun doRequest(sdp: String) {
        logger.debug { "doRequest: $sdp" }
        logger.debug { "doRequest: streamurl $streamUrl, play ${play.get()}" }
        val srsRequestBean = SrsRequestBean()
        srsRequestBean.api =
            if (play.get()) "${SrsConstant.SRS_SERVER_HTTPS}/rtc/v1/play" else "${SrsConstant.SRS_SERVER_HTTPS}/rtc/v1/publish"
        srsRequestBean.streamurl = streamUrl
        srsRequestBean.sdp = sdp
        logger.debug { "doRequest: $srsRequestBean" }
        launchExceptionHandler(WebRTCScope, Dispatchers.IO, {
            val mediaType = MEDIA_TYPE_JSON.toMediaType()
            val requestBody = GsonUtils.toJson(srsRequestBean).toRequestBody(mediaType)
            val srsResponseBean = if (play.get()) {
                SrsHttpManger.srsApiService.play(requestBody)
            } else {
                SrsHttpManger.srsApiService.publish(requestBody)
            }
            logger.debug { "doRequest: $srsResponseBean" }
            srsResponseBean.sdp?.takeIf { it.isNotEmpty() }?.let {
                peerConnection.setRemoteDescription(
                    this@WebRtcHelper,
                    SessionDescription(SessionDescription.Type.ANSWER, it)
                )
            }

        }, onError = {
            logger.error {
                "doRequest: onError:${it.message}"
            }
        }, onComplete = {
            logger.debug { "doRequest: onComplete" }
        })
    }


    //SdpObserver
    override fun onCreateSuccess(sdp: SessionDescription) {
        logger.debug { "onCreateSuccess:$sdp" }
        if (sdp.type == SessionDescription.Type.OFFER) {
            //设置setLocalDescription offer返回sdp
            peerConnection.setLocalDescription(this, sdp)
            if (!TextUtils.isEmpty(sdp.description)) {
                doRequest(sdp.description)
            }
        }
    }

    override fun onSetSuccess() {
        logger.debug { "onSetSuccess:" }
    }

    override fun onCreateFailure(p0: String?) {
        logger.debug { "onCreateFailure:$p0" }
    }

    override fun onSetFailure(p0: String?) {
        logger.debug { "onSetFailure:$p0" }
    }


    //PeerConnection.Observer
    override fun onSignalingChange(p0: PeerConnection.SignalingState?) {
        logger.debug { "onSignalingChange: $p0" }
    }

    override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState?) {
        logger.debug { "onIceConnectionChange: $p0" }
    }

    override fun onIceConnectionReceivingChange(p0: Boolean) {
        logger.debug { "onIceConnectionReceivingChange: $p0" }
    }

    override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {
        logger.debug { "onIceGatheringChange: $p0" }
    }

    override fun onIceCandidate(iceCandidate: IceCandidate?) {
        logger.debug { "onIceGatheringChange: $iceCandidate" }
        peerConnection.addIceCandidate(iceCandidate)
    }

    override fun onIceCandidatesRemoved(candidates: Array<out IceCandidate>?) {
        logger.debug { "onIceGatheringChange: $candidates" }
        peerConnection.removeIceCandidates(candidates)
    }

    override fun onAddStream(p0: MediaStream?) {
        logger.debug { "onAddStream: $p0" }
    }

    override fun onRemoveStream(p0: MediaStream?) {
        logger.debug { "onRemoveStream: $p0" }
    }

    override fun onDataChannel(p0: DataChannel?) {
        logger.debug { "onDataChannel: $p0" }
    }

    override fun onRenegotiationNeeded() {
        logger.debug { "onRenegotiationNeeded:" }
    }

    override fun onAddTrack(receiver: RtpReceiver, mediaStreams: Array<out MediaStream>) {
        logger.debug { "onAddTrack: ${play.get()}, $receiver, $mediaStreams" }
        //如果是play，则远程的媒体流轨添加videoSink(surfaceViewRenderer),进行画面渲染
        videoSink?.takeIf { play.get() }?.let {
            val remoteMediaStreamTrack: MediaStreamTrack? = receiver.track()
            if (remoteMediaStreamTrack is VideoTrack) {
                remoteMediaStreamTrack.setEnabled(true)
                logger.debug { "remoteMediaStreamTrack.addSink($it)" }
                remoteMediaStreamTrack.addSink(ProxyVideoSink(surfaceViewRenderer))
            }
        }
    }

    inner class ProxyVideoSink(private var target: VideoSink) : VideoSink {
        override fun onFrame(videoFrame: VideoFrame) {
            logger.debug {
                "ProxyVideoSink onFrame width ${videoFrame.buffer.width}， height ${videoFrame.buffer.width}"
                target.onFrame(videoFrame)
            }
        }
    }
}