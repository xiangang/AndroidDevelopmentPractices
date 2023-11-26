package com.nxg.im.chat.component.conversation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.nxg.im.chat.R
import com.nxg.im.core.utils.VideoUtils

/**
 * 全屏视频播放
 * @param modifier Modifier
 * @param url String
 * @param onClick Function0<Unit>  单击图片
 */
@Composable
fun FullScreenVideo(
    modifier: Modifier = Modifier,
    url: String,
    thumbnailUrl: String,
    onClick: () -> Unit = {}
) {
    val alpha by remember { mutableFloatStateOf(1f) }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = alpha))
    ) {
        AsyncImage(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize(),
            model = thumbnailUrl,
            contentDescription = thumbnailUrl,
        )

        VideoPlayer(
            modifier = Modifier.align(Alignment.Center), url
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
