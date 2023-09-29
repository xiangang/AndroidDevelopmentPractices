package com.nxg.im.core.module.videocall

import com.nxg.im.core.IMService
import com.nxg.im.core.data.bean.Session

interface VideoCallService : IMService {

    /**
     * 返回Session
     */
    fun getSession(sessionId: Long): Session?

    /**
     * 缓存session
     */
    fun putSession(session: Session)

    /**
     * 移除session
     */
    fun removeSession(session: Session)

    /**
     * 呼叫
     */
    fun call(vararg toIds: Long)

    /**
     * 取消
     */
    fun cancel(session: Session)

    /**
     * 接听
     */
    fun answer(session: Session)

    /**
     * 挂断
     */
    fun hangup(session: Session)

}