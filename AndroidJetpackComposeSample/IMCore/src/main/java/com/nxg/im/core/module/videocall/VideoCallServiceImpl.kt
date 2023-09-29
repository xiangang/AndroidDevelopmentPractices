package com.nxg.im.core.module.videocall

import com.nxg.im.core.IMClient
import com.nxg.im.core.data.bean.Session
import com.nxg.im.core.dispatcher.IMCoroutineScope
import com.nxg.im.core.module.auth.AuthService
import com.nxg.im.core.module.signaling.SignalingHelper
import com.nxg.im.core.module.signaling.SignalingService
import com.nxg.im.core.module.signaling.SignalingType
import com.nxg.im.core.module.state.VideoCallStateMachine
import com.nxg.mvvm.logger.SimpleLogger
import kotlinx.coroutines.launch

/**
 * 视频通话服务实现类
 */
object VideoCallServiceImpl : VideoCallService, SimpleLogger {

    private val sessions = hashMapOf<Long, Session>()

    override fun getSession(sessionId: Long): Session? {
        return sessions[sessionId]
    }

    override fun putSession(session: Session) {
        sessions[session.sessionId] = session
    }

    override fun removeSession(session: Session) {
        sessions.remove(session.sessionId)
    }

    override fun call(vararg toIds: Long) {
        logger.debug { "call: $toIds" }
        IMCoroutineScope.launch {
            IMClient.authService.getLoginData()?.let { loginData ->
                IMClient.signalingService.generateUid()?.let { uuid ->
                    IMClient.signalingService
                        .sendSignaling(
                            SignalingHelper.createVideoCallInvite(
                                uuid,
                                loginData.user.uuid,
                                toIds.toMutableSet().apply { add(loginData.user.uuid) }
                            )
                        )
                }
            }
        }

    }

    override fun cancel(session: Session) {
        logger.debug { "cancel: $session" }
        IMCoroutineScope.launch {
            IMClient.authService.getLoginData()?.let {
                IMClient.signalingService
                    .sendSignaling(
                        session.signaling.cancel(it.user.uuid)
                    )
            }
        }
    }

    override fun answer(session: Session) {
        logger.debug { "answer: $session" }
        IMCoroutineScope.launch {
            IMClient.authService.getLoginData()?.let {
                IMClient.signalingService
                    .sendSignaling(
                        session.signaling.answer(it.user.uuid)
                    )
            }
        }
    }

    override fun hangup(session: Session) {
        logger.debug { "hangup: $session" }
        IMCoroutineScope.launch {
            IMClient.authService.getLoginData()?.let {
                IMClient.signalingService
                    .sendSignaling(
                        session.signaling.bye(it.user.uuid)
                    )
            }
        }
    }
}