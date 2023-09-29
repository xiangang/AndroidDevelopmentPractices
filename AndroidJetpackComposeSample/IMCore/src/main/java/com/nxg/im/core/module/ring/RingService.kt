package com.nxg.im.core.module.ring

import android.media.Ringtone
import android.media.RingtoneManager
import com.blankj.utilcode.util.Utils
import com.nxg.im.core.IMService


interface RingService : IMService {

    fun playRing()

    fun stopRing()
}

object RingServiceImpl : RingService {

    private var ringtone: Ringtone? = null

    override fun playRing() {
        val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        ringtone = RingtoneManager.getRingtone(Utils.getApp(), notification)
        ringtone?.play()
    }

    override fun stopRing() {
        ringtone?.takeIf { it.isPlaying }?.stop()
    }
}