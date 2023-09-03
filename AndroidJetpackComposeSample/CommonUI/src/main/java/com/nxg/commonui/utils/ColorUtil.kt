package com.nxg.commonui.utils

import androidx.compose.ui.graphics.Color


/**
 * rgb转换成16进制
 * @return
 */
fun Color.toHexString(): String {
    return String.format(
        "#%02X%02X%02X",
        (red * 255.0f).toInt(),
        (green * 255.0f).toInt(),
        (blue * 255.0f).toInt()
    )
}

