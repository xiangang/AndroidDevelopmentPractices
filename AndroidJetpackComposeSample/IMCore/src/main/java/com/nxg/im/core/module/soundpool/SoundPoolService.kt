package com.nxg.im.core.module.soundpool

import androidx.annotation.RawRes
import com.blankj.utilcode.util.Utils
import com.nxg.im.core.IMService
import com.nxg.im.core.R
import com.nxg.im.core.utils.SoundPoolUtil

interface SoundPoolService : IMService {

    fun init()

    fun play(@RawRes resId: Int, loop: Int = 0)
    fun stop(@RawRes resId: Int)
}

//音效
val SoundPoolRawRes = listOf(
    R.raw.im_core_ring,
    R.raw.im_core_hangup,
    R.raw.im_core_new_msg
)

object SoundPoolServiceImpl : SoundPoolService {

    override fun init() {
        SoundPoolRawRes.forEach {
            SoundPoolUtil.getInstance(Utils.getApp()).load(it)
        }
    }

    override fun play(resId: Int, loop: Int) {
        SoundPoolUtil.getInstance(Utils.getApp()).playByRes(soundResId = resId, loop = loop)
    }

    override fun stop(@RawRes resId: Int) {
        //SoundPoolUtil.getInstance(Utils.getApp()).stop(resId)
        SoundPoolUtil.getInstance(Utils.getApp()).release()
    }
}