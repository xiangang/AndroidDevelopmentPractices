package com.nxg.webrtcmobile.audiocall

import androidx.lifecycle.ViewModel

class AudioCallViewModel : ViewModel() {

    fun pushH264(h264FilePath: String) {
        // 创建WebRTC PeerConnection
        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(context)
                .createInitializationOptions()
        )
        val factory: PeerConnectionFactory =
            PeerConnectionFactory.builder().createPeerConnectionFactory()
        val constraints = MediaConstraints()
        val peerConnection: PeerConnection = factory.createPeerConnection(null, constraints, null)
        // 读取H264文件
        val fis = FileInputStream(h264FilePath)
        val buffer = ByteArray(1024)
        var len: Int
        while (fis.read(buffer).also { len = it } > 0) {
            val videoData: ByteArray = Arrays.copyOf(buffer, len)
            // 封装视频数据
            val packet: ByteArray = encoder.encode(videoData)
            // 将封装后的视频数据发送给远端
            peerConnection.send(packet)
        }
        // 关闭PeerConnection
        peerConnection.close()
        factory.dispose()
        PeerConnectionFactory.stopInternalTracingCapture()
        PeerConnectionFactory.shutdownInternalTracer()

    }