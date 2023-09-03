package com.nxg.commonui.utils

import android.content.res.Resources

object DensityUtil {

    fun dp2px(resources: Resources, dp: Int): Int? {
        return resources.displayMetrics?.density?.let { (dp * it + 0.5).toInt() }
    }

    fun px2dp(resources: Resources, px: Int): Int? {
        return resources.displayMetrics?.density?.let { (px.toDouble() / it + 0.5).toInt() }
    }
}
