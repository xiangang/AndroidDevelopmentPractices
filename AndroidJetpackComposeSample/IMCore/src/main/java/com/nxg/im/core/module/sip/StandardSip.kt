package com.nxg.im.core.module.sip


import okhttp3.WebSocket
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * 标准SIP的信令设计遵循以下原则：

 * 1. SIP信令采用文本格式，使用ASCII码表示，易于阅读和调试。
 * 2. SIP信令使用HTTP风格的请求和响应，包括请求方法、URI、协议版本、消息头和消息体等元素。
 * 3. SIP信令采用分层结构，包括用户层、事务层和传输层。用户层负责处理应用层的请求和响应，事务层负责处理SIP事务的状态和转换，传输层负责处理SIP消息的传输和重传。
 * 4. SIP信令支持多种请求方法，包括INVITE、ACK、BYE、CANCEL、REGISTER、OPTIONS、INFO、PRACK、SUBSCRIBE、NOTIFY、REFER等，每个方法具有特定的语义和用途。
 * 5. SIP信令采用状态机模型，定义了SIP事务的状态转换和超时处理机制，保证了SIP信令的可靠性和稳定性。
 * 6. SIP信令支持请求和响应的路由和转发，包括通过代理服务器、重定向和Location服务等方式实现。
 * 7. SIP信令支持会话描述协议SDP，用于描述媒体流的类型、格式、编码和传输参数等信息。
 * 总之，标准SIP的信令设计具有灵活、可扩展、可靠、可路由和可互操作性等优点，适用于各种IP通信场景。
 *
 * 以下是使用Ktor Websocket仿造标准SIP的信令设计一个通话系统：
 *
 * `CallSession`类代表一个通话会话，
 * `CallState`枚举代表通话状态，
 * `SipMessage`类代表SIP信令消息，
 * `SipMethod`枚举代表SIP请求方法，
 * `SipStatus`类代表SIP响应状态码，
 * `SipHeader`类代表SIP消息头字段。、
 * 在`routing`函数中定义了一个`/sip`的Websocket路由。
 * 当客户端连接到该路由时，创建一个`CallSession`对象表示一个新的通话会话，并将其添加到`callSessions`中。
 * 在`CallSession`类中，`handleMessage`函数用于处理收到的SIP信令消息。
 * 根据SIP请求方法的不同，调用相应的处理函数，如`handleInvite`、`handleAck`和`handleBye`。
 * 处理函数根据当前通话状态和请求内容，生成相应的SIP响应消息，并将其发送给客户端。
 *
 * 在`SipMessage`类中，
 * `createRequest`函数用于创建SIP请求消息，
 * `createResponse`函数用于创建SIP响应消息，
 * `parse`函数用于解析SIP消息文本，
 * `toString`函数用于将SIP消息对象转换为文本。
 *
 * 在`SipMethod`枚举中，`fromValue`函数用于将SIP请求方法的字符串表示转换为枚举值。
 * 在`SipStatus`类中，定义了多个常用的SIP响应状态码。
 * 在`SipHeader`类中，定义了多个常用的SIP消息头字段。
 */
data class CallSession(val callId: String, val webSocket: WebSocket) {
    private var state: CallState = CallState.Idle

    suspend fun handleMessage(message: String) {
        val request = SipMessage.parse(message)
        val response = when (request.sipMethod) {
            SipMethod.INVITE -> handleInvite(request)
            SipMethod.ACK -> handleAck(request)
            SipMethod.BYE -> handleBye(request)
            else -> SipMessage.createResponse(request, SipStatus.BAD_REQUEST)
        }

        webSocket.send(response.toString())
    }

    private fun handleInvite(request: SipMessage): SipMessage {
        if (state != CallState.Idle) {
            return SipMessage.createResponse(request, SipStatus.BUSY_HERE)
        }

        state = CallState.Ringing
        val response = SipMessage.createResponse(request, SipStatus.RINGING)
        response.headers[SipHeader.CONTACT] = "<$webSocket>"
        return response
    }

    private fun handleAck(request: SipMessage): SipMessage {
        if (state != CallState.Ringing) {
            return SipMessage.createResponse(request, SipStatus.CALL_OR_TRANSACTION_DOES_NOT_EXIST)
        }

        state = CallState.Active
        return SipMessage.createResponse(request, SipStatus.OK)
    }

    private fun handleBye(request: SipMessage): SipMessage {
        if (state != CallState.Active) {
            return SipMessage.createResponse(request, SipStatus.CALL_OR_TRANSACTION_DOES_NOT_EXIST)
        }

        state = CallState.Idle
        return SipMessage.createResponse(request, SipStatus.OK)
    }

    suspend fun close() {
        if (state != CallState.Idle) {
            val request = SipMessage.createRequest(
                SipMethod.BYE,
                "192.168.1.5",
                "sip:1@192.168.1.5",
                "sip:2@192.168.1.5"
            )
            val response = handleBye(request)
            webSocket.send(response.toString())
        }
        webSocket.close(1000, "close ")
    }
}

enum class CallState {
    Idle,
    Ringing,
    Active
}

fun callSessionId(): String {
    return "call-${System.currentTimeMillis()}"
}

class SipMessage(
    val sipMethod: SipMethod,
    val uri: String,
    val headers: MutableMap<String, String> = mutableMapOf(),
    val body: String? = null,
    val sipStatus: SipStatus? = null,
) {
    companion object {

        val seq = AtomicInteger(0)

        fun getCSeq(): Int {
            val cseq = seq.getAndIncrement()
            if (seq.get() >= 65535) {
                seq.set(0)
            }
            return cseq
        }

        fun createRequest(sipMethod: SipMethod, uri: String, from: String, to: String): SipMessage {
            return SipMessage(
                sipMethod,
                uri,
                mutableMapOf(
                    SipHeader.VIA to "SIP/2.0/UDP 192.168.1.5:1985;",
                    SipHeader.FROM to "sip:1@192.168.1.5",
                    SipHeader.TO to "sip:2@192.168.1.5",
                    SipHeader.CALL_ID to callSessionId(),
                    SipHeader.CSEQ to "${getCSeq()} ${sipMethod.name}",
                    SipHeader.USER_AGENT to "Ktor",
                    SipHeader.CONTACT to "<sip:1@192.168.1.5:1985>",
                    SipHeader.CONTENT_TYPE to "application/sdp"
                ),
                null
            )
        }

        fun createResponse(sipMessage: SipMessage, sipStatus: SipStatus): SipMessage {
            return SipMessage(
                SipMethod.SIP,
                sipMessage.uri,
                mutableMapOf(
                    SipHeader.VIA to "SIP/2.0/UDP 192.168.1.5:1985;",
                    SipHeader.FROM to sipMessage.headers[SipHeader.TO]!!,
                    SipHeader.TO to sipMessage.headers[SipHeader.FROM]!!,
                    SipHeader.CALL_ID to sipMessage.headers[SipHeader.CALL_ID]!!,
                    SipHeader.CSEQ to "${sipMessage.headers[SipHeader.CSEQ]!!.split(" ")[0]} ${sipStatus.code} ${sipStatus.reasonPhrase}",
                    SipHeader.USER_AGENT to "Ktor",
                    SipHeader.ACCEPT to "application/sdp",
                    SipHeader.ALLOW to "INVITE, ACK, BYE, CANCEL, OPTIONS, MESSAGE, INFO, UPDATE, REGISTER, REFER, NOTIFY, PUBLISH, SUBSCRIBE",
                    SipHeader.CONTACT to "<sip:1@192.168.1.5:1985>",
                    SipHeader.CONTENT_TYPE to "application/sdp"
                ),
                null,
                sipStatus
            )
        }

        fun parse(text: String): SipMessage {
            val lines = text.lines().map { it.trim() }
            val method =
                SipMethod.fromValue(lines[0].substringBefore(" ").uppercase(Locale.getDefault()))
            val uri = lines[0].substringAfter(" ")
            val headers = mutableMapOf<String, String>()
            var body: String? = null
            for (line in lines.drop(1)) {
                if (line.isBlank()) {
                    body = lines.dropWhile { it.isNotBlank() }.joinToString("\n")
                    break
                }
                val key = line.substringBefore(":").uppercase(Locale.getDefault())
                val value = line.substringAfter(":").trim()

                if (key != SipHeader.CONTENT_LENGTH) {
                    headers[key] = value
                }
            }

            return SipMessage(method, uri, headers, body)
        }
    }

    override fun toString(): String {
        val lines = mutableListOf<String>()
        if (sipMethod == SipMethod.SIP) {
            lines.add("$sipMethod ${sipStatus?.code} ${sipStatus?.reasonPhrase}")
        } else {
            lines.add("$sipMethod $uri SIP/2.0")
        }
        for ((key, value) in headers) {
            lines.add("$key: $value")
        }
        var contentLength = 0
        for (line in lines) {
            contentLength += line.length
        }
        lines.add("${SipHeader.CONTENT_LENGTH}: $contentLength")
        if (body != null) {
            lines.add("")
            lines.add(body)
        } else {
            lines.add("")
        }
        return lines.joinToString("\r\n")
    }
}

/**
 * 标准SIP信令包括以下内容：
 *
 * 1. INVITE：邀请对方建立会话。
 * 2. ACK：确认对方已收到INVITE请求。
 * 3. BYE：结束会话。
 * 4. CANCEL：取消正在进行的呼叫。
 * 5. REGISTER：向SIP服务器注册用户信息。
 * 6. OPTIONS：查询SIP服务器支持的功能。
 */
enum class SipMethod(val value: String) {
    INVITE("INVITE"),//用于与用户代理之间的媒体交换建立对话。
    ACK("ACK"),//客户端向服务器端证实它已经收到了对INVITE请求的最终响应。
    BYE("BYE"),//表示终止一个已经建立的呼叫。
    CANCEL("CANCEL"),//表示在收到对请求的最终响应之前取消该请求，对于已完成的请求则无影响。
    REGISTER("REGISTER"),//该方法为用户代理实施位置服务，该位置服务向服务器指示其地址信息。
    OPTIONS("OPTIONS"),//表示查询被叫的相关信息和功能。
    MESSAGE("MESSAGE"),
    PRACK("PRACK"),//表示对1xx响应消息的确认请求消息。
    INFO("INFO"),
    REFER("REFER"),
    SUBSCRIBE("SUBSCRIBE"),
    NOTIFY("NOTIFY"),
    UPDATE("UPDATE"),
    SIP("SIP/2.0");

    companion object {
        fun fromValue(value: String): SipMethod {
            return values().firstOrNull { it.value == value }
                ?: throw IllegalArgumentException("Invalid SIP method: $value")
        }
    }
}

class SipStatus(val code: Int, val reasonPhrase: String) {
    companion object {
        val OK = SipStatus(200, "OK")
        val RINGING = SipStatus(180, "Ringing")
        val BUSY_HERE = SipStatus(486, "Busy Here")
        val CALL_OR_TRANSACTION_DOES_NOT_EXIST = SipStatus(481, "Call/Transaction Does Not Exist")
        val BAD_REQUEST = SipStatus(400, "Bad Request")
    }

    override fun toString(): String {
        return "$code $reasonPhrase"
    }
}

/**
 * via：SIP版本号（2.0）、传输类型（UDP）、呼叫地址 、branch。branch为分支，是一随机码，它被看作传输标识，标志会话事务。
 * 　　<＝Via字段中地址是消息发送方或代理转发方设备地址，一般由主机地址和端口号组成
 * 　　<＝传输类型可以为UDP、TCP、TLS、SCTP
 *
 * From：表示请求消息的发送方和目标方
 * 　　<＝如果里面有用户名标签，地址要求用尖括号包起来
 * 　　<＝对于INVITE消息，可以在From字段中包含tag，它也是个随机码
 *
 * To：请求消息的目标方
 * Contact：是INVITE消息所必须的，用来告诉对方，回消息给谁。**
 * 　　<＝（注意区别：在180RINGING的时候，这里是目标方地址）**
 * Call-ID：用于标识一个特定邀请以及与这个邀请相关的所有后续事务（即标识一个会话）
 * CSeq：字段是用来给同一个会话中的事务进行排序的，每发送一个新的请求，该值加1，当排序到65535（也就是216的最大数），会开始新一轮的排序。
 * Allow：允许的请求消息类型
 * Supported：
 * Session-Expires：存活时间，用户的响应必须在这个时间范围内
 * Min-SE：最小长度。
 * X-UCM-AudioRecord：自定义字段
 * X-UCM-CallPark：自定义字段
 * Max-Forwards：最大转发数量限制了通讯中转发的数量。它是由一个整数组成，每转发一次，整数减一。如果在请求消息到达目的地之前该值变为零，那么请求将被拒绝并返回一个483（跳数过多）错误响应消息。
 * User-Agent：指明UA的用户类型
 * Content-Type：消息实体类型
 * Content-Length：消息实体长度，单位为字节
 */
class SipHeader {
    companion object {
        const val CALL_ID = "Call-ID"
        const val VIA = "Via"
        const val FROM = "From"
        const val TO = "To"
        const val CSEQ = "CSeq"
        const val USER_AGENT = "User-Agent"
        const val MAX_FORWARDS = "Max-Forwards"
        const val CONTACT = "Contact"
        const val ACCEPT = "Accept"
        const val ALLOW = "Allow"
        const val CONTENT_TYPE = "Content-Type"
        const val CONTENT_LENGTH = "Content-Length"
        const val SIP = "SIP"
    }
}
