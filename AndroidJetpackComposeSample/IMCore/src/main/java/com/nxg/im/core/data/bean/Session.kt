package com.nxg.im.core.data.bean

import com.nxg.im.core.module.signaling.Signaling

data class Session constructor(val sessionId: Long,val signaling: Signaling)