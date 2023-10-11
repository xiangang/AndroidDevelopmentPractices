package com.nxg.commonlib.utils

import androidx.annotation.StringRes
import com.blankj.utilcode.util.Utils

object StringUtils {

    fun getString(@StringRes resID: Int): String {
        return Utils.getApp().getString(resID)
    }

}