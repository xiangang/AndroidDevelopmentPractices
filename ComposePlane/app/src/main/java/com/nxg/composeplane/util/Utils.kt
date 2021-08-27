package com.nxg.composeplane.util

import android.app.Activity
import android.content.res.Resources
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nxg.composeplane.R

object StatusBarUtil {
    fun transparentStatusBar(activity: Activity) {
        with(activity) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            val option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            val vis = window.decorView.systemUiVisibility
            window.decorView.systemUiVisibility = option or vis
            window.statusBarColor = Color.TRANSPARENT
        }
    }
}

object DensityUtil {

    fun dxToDp(resources: Resources, px: Int): Int =
        (px / resources.displayMetrics.density + 0.5f).toInt()

    fun dp2px(resources: Resources, dp: Int): Int? {
        return resources.displayMetrics?.density?.let { (dp * it + 0.5).toInt() }
    }

    fun px2dp(resources: Resources, px: Int): Int? {
        return resources.displayMetrics?.density?.let { (px.toDouble() / it + 0.5).toInt() }
    }
}

object ValueUtil {
    fun getRandomDp(fromDp: Dp, toDp: Dp): Dp =
        (fromDp.value.toInt()..toDp.value.toInt()).random().dp
}

object LogUtil {
    fun printLog(tag: String = "ComposePlane", message: String) {
        Log.d(tag, message)
    }
}

/**
 * 精灵工具类
 */
object SpriteUtil {

    /**
     * 矩形碰撞的函数
     * @param x1 第一个矩形的X坐标
     * @param y1 第一个矩形的Y坐标
     * @param w1 第一个矩形的宽
     * @param h1 第一个矩形的高
     * @param x2 第二个矩形的X坐标
     * @param y2 第二个矩形的Y坐标
     * @param w2 第二个矩形的宽
     * @param h2 第二个矩形的高
     */
    fun isCollisionWithRect(
        x1: Int,
        y1: Int,
        w1: Int,
        h1: Int,
        x2: Int,
        y2: Int,
        w2: Int,
        h2: Int
    ): Boolean {
        if (x1 >= x2 && x1 >= x2 + w2) {
            return false
        } else if (x1 <= x2 && x1 + w1 <= x2) {
            return false
        } else if (y1 >= y2 && y1 >= y2 + h2) {
            return false
        } else if (y1 <= y2 && y1 + h1 <= y2) {
            return false
        }
        return true
    }

}


val ScoreFontFamily = FontFamily(
    Font(R.font.riskofrainsquare, FontWeight.Bold)
)

//创建字体
val CookiesFontFamily = FontFamily(
    Font(R.font.cookies, FontWeight.Bold)
)
