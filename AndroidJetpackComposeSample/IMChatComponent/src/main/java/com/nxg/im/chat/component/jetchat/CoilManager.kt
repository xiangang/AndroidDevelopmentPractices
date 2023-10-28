package com.nxg.im.chat.component.jetchat

import android.content.Context
import android.os.Build
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.Coil
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import github.leavesczy.matisse.ImageEngine
import github.leavesczy.matisse.MediaResource
import kotlinx.parcelize.Parcelize

object CoilManager {

    fun initCoil(context: Context) {
        val imageLoader = ImageLoader.Builder(context = context)
            .components {
                //可选，需要展示 Gif 则引入
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
                //可选，需要展示 Video 则引入
                //add(VideoFrameDecoder.Factory())
            }
            .build()
        Coil.setImageLoader(imageLoader)
    }
}

@Parcelize
class CoilImageEngine : ImageEngine {

    @Composable
    override fun Thumbnail(
        modifier: Modifier,
        mediaResource: MediaResource,
        contentScale: ContentScale
    ) {
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = mediaResource.uri,
            contentScale = ContentScale.Crop,
            contentDescription = mediaResource.name
        )
    }

    @Composable
    override fun Image(
        modifier: Modifier,
        mediaResource: MediaResource,
        contentScale: ContentScale
    ) {
        if (mediaResource.isVideo) {
            AsyncImage(
                modifier = Modifier.fillMaxWidth(),
                model = mediaResource.uri,
                contentScale = ContentScale.FillWidth,
                contentDescription = mediaResource.name
            )
        } else {
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(state = rememberScrollState()),
                model = mediaResource.uri,
                contentScale = ContentScale.FillWidth,
                contentDescription = mediaResource.name
            )
        }
    }

}