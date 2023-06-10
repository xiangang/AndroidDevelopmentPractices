package com.nxg.commonui.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.Type.ime
import androidx.core.view.WindowInsetsCompat.Type.statusBars
import androidx.core.view.WindowInsetsControllerCompat
import java.util.*

object ActivityUtil {

    fun callPhone(context: Context, phoneNum: String) {
        val intent = Intent()
        intent.action = Intent.ACTION_CALL
        intent.data = Uri.parse("tel:$phoneNum")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.applicationContext.startActivity(intent)
    }

    fun jumpBrowser(context: Context, url: String) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.data = Uri.parse(url)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.applicationContext.startActivity(intent)
    }

}

/**
 * 获取当前是否为深色模式
 * 深色模式的值为:0x21
 * 浅色模式的值为:0x11
 * @return true 为是深色模式   false为不是深色模式
 */
fun Context.isDarkMode(): Boolean {
    return resources.configuration.uiMode == 0x21
}

/**
 * 设置透明状态栏
 */
fun Activity.transparentStatusBar() {
    // 隐藏状态栏
    WindowCompat.getInsetsController(window, window.decorView).hide(statusBars())
    // 设置状态栏颜色为透明
    window.statusBarColor = Color.TRANSPARENT
}

/**
 * 设置状态栏颜色
 */
fun Activity.transparentStatusBar(@ColorInt color: Int) {
    // 隐藏状态栏
    WindowCompat.getInsetsController(window, window.decorView).hide(statusBars())
    // 设置状态栏颜色为color
    window.statusBarColor = color
}

/**
 * 设置状态栏模式：Light OR Dark
 */
fun Activity.setAndroidNativeLightStatusBar(isLight: Boolean) {
    WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = isLight
}

/**
 * 隐藏系统UI
 * WindowInsetsCompat.Type.systemBars()表示状态栏、导航栏和标题栏（ STATUS_BARS | NAVIGATION_BARS | CAPTION_BAR）
 */
fun Activity.hideSystemBars() {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    WindowCompat.getInsetsController(window, window.decorView).let { controller ->
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}

/**
 * 显示系统UI
 */
fun Activity.showSystemBars() {
    WindowCompat.setDecorFitsSystemWindows(window, true)
    WindowCompat.getInsetsController(window, window.decorView)
        .show(WindowInsetsCompat.Type.systemBars())
}

/**
 * 隐藏ime
 */
fun Activity.hideIme() {
    WindowCompat.getInsetsController(window, window.decorView).hide(ime())
}

/**
 * 显示ime
 */
fun Activity.showIme() {
    WindowCompat.getInsetsController(window, window.decorView).show(ime())
}

