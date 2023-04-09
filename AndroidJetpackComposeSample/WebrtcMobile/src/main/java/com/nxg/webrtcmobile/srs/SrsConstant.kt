package com.nxg.webrtcmobile.srs

object SrsConstant {

    /**
     * SRS服务器IP
     */
    const val SRS_SERVER_IP = "192.168.1.5"

    /**
     * SRS服务http请求端口，默认1985
     */
    private const val SRS_SERVER_HTTP_PORT = "1985"

    /**
     * SRS服务https请求端口，默认1990
     */
    private const val SRS_SERVER_HTTPS_PORT = "1990"

    const val SRS_SERVER_HTTP = "http://$SRS_SERVER_IP:$SRS_SERVER_HTTP_PORT"

    const val SRS_SERVER_HTTPS = "https://$SRS_SERVER_IP:$SRS_SERVER_HTTPS_PORT"
}