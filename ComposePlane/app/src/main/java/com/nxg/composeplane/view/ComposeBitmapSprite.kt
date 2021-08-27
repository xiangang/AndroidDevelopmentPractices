package com.nxg.composeplane.view

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.TypedValue
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource

/**
 * 这里直接返回bitmap
 * 方便剪裁和缩放
 */
@SuppressLint("UseCompatLoadingForDrawables")
fun imageResource(res: Resources, @DrawableRes id: Int): Bitmap {
    return (res.getDrawable(id, null) as BitmapDrawable).bitmap
}

/**
 * Load an ImageBitmap from an image resource.
 *
 * This function is intended to be used for when low-level ImageBitmap-specific
 * functionality is required.  For simply displaying onscreen, the vector/bitmap-agnostic
 * [painterResource] is recommended instead.
 *
 * @param id the resource identifier
 * @return the decoded image data associated with the resource
 */
@Composable
fun imageResource(@DrawableRes id: Int): Bitmap {
    val context = LocalContext.current
    val value = remember { TypedValue() }
    context.resources.getValue(id, value, true)
    val key = value.string!!.toString() // image resource must have resource path.
    return remember(key) { imageResource(context.resources, id) }
}