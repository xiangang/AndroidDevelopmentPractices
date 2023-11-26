package com.nxg.im.chat.component.conversation

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.nxg.im.chat.R

/**
 * 全屏图片查看
 * @param modifier Modifier
 * @param url String
 * @param onClick Function0<Unit>  单击图片
 */
@Composable
fun FullScreenImage(
    modifier: Modifier = Modifier,
    url: String,
    onClick: () -> Unit = {}
) {
    val widthPixels = LocalContext.current.resources.displayMetrics.widthPixels
    val heightPixels = LocalContext.current.resources.displayMetrics.heightPixels
    // set up all transformation states
    var enableBack by remember { mutableStateOf(true) }
    var scale by remember { mutableFloatStateOf(1f) }
    var rotation by remember { mutableFloatStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var alpha by remember { mutableFloatStateOf(1f) }
    val imageRect = remember { mutableStateOf(Rect.Zero) }
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale *= zoomChange
        rotation += rotationChange
        offset += offsetChange
        if (scale != 1f || rotation != 0f) {
            enableBack = false
        }
        Log.i("TAG", "offsetX ${offset.x}, offsetY ${offset.y}")
        /*if (enableBack) {
            if (imageRect.value.bottom > 0) {
                val validOffsetTotal = heightPixels - imageRect.value.bottom
                val tempAlpha =
                    (validOffsetTotal - offset.y) / (heightPixels - imageRect.value.bottom)
                alpha = if (tempAlpha <= 0f) {
                    0f
                } else if (tempAlpha >= 1f) {
                    1f
                } else {
                    tempAlpha
                }
                if (alpha < 0.4f) {
                    onClick()
                }
            }
        } else {
            alpha = 1f
        }*/
    }


    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = alpha))
    ) {

        /*Text(
            text = "widthPixels: $widthPixels, heightPixels: $heightPixels," +
                    " alpha $alpha," +
                    " Left: ${imageRect.value.left}, Top: ${imageRect.value.top}," +
                    " Right: ${imageRect.value.right}, Bottom: ${imageRect.value.bottom}",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 60.dp),
            color = Color.White
        )*/

        Image(
            painter = rememberAsyncImagePainter(url),
            contentDescription = "",
            modifier = Modifier
                .align(Alignment.Center)
                // apply other transformations like rotation and zoom
                // on the pizza slice emoji
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    rotationZ = rotation,
                    translationX = offset.x,
                    translationY = offset.y
                )
                // add transformable to listen to multitouch transformation events
                // after offset
                .transformable(state = state)
                .wrapContentSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            scale = 1f
                            rotation = 0f
                            offset = Offset.Zero
                            enableBack = true
                        },
                        onTap = {
                            Log.i("TAG", "FullScreenImage: onTap")
                        },
                        onPress = { offset ->
                            // 手指按下事件
                            Log.i("TAG", "FullScreenImage: onPress：$offset")
                        }
                    )
                }
                .onGloballyPositioned { coordinates ->
                    Log.i("TAG", "FullScreenImage: coordinates")
                    imageRect.value = coordinates.boundsInParent()
                }
        )


        Icon(
            imageVector = Icons.Filled.Close,
            tint = Color.White,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .clickable(onClick = {
                    onClick()
                })
                .padding(horizontal = 16.dp, vertical = 50.dp)
                .height(50.dp),
            contentDescription = stringResource(id = R.string.close)
        )
    }

}
