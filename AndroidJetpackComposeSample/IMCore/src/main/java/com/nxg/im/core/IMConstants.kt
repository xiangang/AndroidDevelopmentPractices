package com.nxg.im.core

object IMConstants {

    /**
     * IM服务器IP
     */
    const val IM_SERVER_IP = "192.168.1.5"

    /**
     * IM服务http请求端口，默认8080
     */
    const val IM_SERVER_HTTP_PORT = "8050"

    /**
     * IM服务https请求端口，默认8081
     */
    const val IM_SERVER_HTTPS_PORT = "8051"

    /**
     * IM服务器http接口
     */
    const val IM_SERVER_HTTP = "http://$IM_SERVER_IP:$IM_SERVER_HTTP_PORT"

    /**
     * IM服务器https接口
     */
    const val IM_SERVER_HTTPS = "https://$IM_SERVER_IP:$IM_SERVER_HTTP_PORT"

    object Api {
        const val MediaTypeJson = "application/json"
        const val Bearer = "Bearer"
        const val Me = "me"
        const val Token = "token"
        const val SecretSharedPrefs = "secret_shared_prefs"
    }

    object Protocol {
        const val Version = 0
        const val TYPE_REQ = 0 //发送方
        const val TYPE_ACK = 1 //服务器应答
        const val TYPE_NOTIFY = 2 //服务器转发

        object Cmd {
            const val Chat = "chat"
            const val Signaling = "signaling"
        }

        object SubCmd {
            const val Text = "text"
            const val VideoCall = "video_call"
            const val AudioCall = "audio_call"
        }

    }


}