package com.nxg.im.core.module.signaling

import com.nxg.im.core.IMCoreMessage
import com.nxg.im.core.IMService
import kotlin.Long

interface SignalingService : IMService {

    fun sendSignaling(signaling: Signaling)

    fun onReceiveSignaling(imCoreMessage: IMCoreMessage, signaling: Signaling)

    suspend fun generateUid(): Long?
}