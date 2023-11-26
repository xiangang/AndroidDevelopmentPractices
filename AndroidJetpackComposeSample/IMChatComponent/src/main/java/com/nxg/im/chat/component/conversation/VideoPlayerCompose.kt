package com.nxg.im.chat.component.conversation

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.nxg.im.core.module.videocall.VideoCallServiceImpl.logger

@Composable
fun VideoPlayer(modifier: Modifier = Modifier, videoUrl: String) {
    val context = LocalContext.current
    val player = remember { ExoPlayer.Builder(context).build() }
    val videoUri = Uri.parse(videoUrl)
    val dataSourceFactory = remember { DefaultDataSource.Factory(context) }
    val mediaSource: MediaSource =
        ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(
            MediaItem.fromUri(videoUri)
        )

    DisposableEffect(videoUri) {
        player.setMediaSource(mediaSource)
        player.prepare()
        player.playWhenReady = true
        onDispose {
            player.stop()
            player.release()
        }
    }

    val playerView = remember {
        val playerView = StyledPlayerView(context)
        playerView.player = player
        playerView
    }
    Box(modifier = modifier.wrapContentSize()) {
        AndroidView(factory = {
            playerView
        }) {
            it.setShowFastForwardButton(false)
            it.setShowRewindButton(false)
            it.setShowPreviousButton(false)
            it.setShowNextButton(false)
        }
        PlayerViewLifecycle(playerView)
    }

    DisposableEffect(videoUri) {
        val showController = Runnable { playerView.hideController() }
        playerView.postDelayed(showController, 100)
        onDispose {
            playerView.removeCallbacks(showController)
        }
    }
}

@Composable
private fun PlayerViewLifecycle(styledPlayerView: StyledPlayerView) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val previousState = remember { mutableStateOf(Lifecycle.Event.ON_CREATE) }
    DisposableEffect(context, lifecycle, styledPlayerView) {
        val mapLifecycleObserver = styledPlayerView.lifecycleObserver(previousState)
        lifecycle.addObserver(mapLifecycleObserver)
        onDispose {
            lifecycle.removeObserver(mapLifecycleObserver)
            // fix memory leak
            styledPlayerView.removeAllViews()
        }
    }
}

private fun StyledPlayerView.lifecycleObserver(previousState: MutableState<Lifecycle.Event>): LifecycleEventObserver =
    LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                logger.debug { "StyledPlayerView: ON_CREATE" }
            }

            Lifecycle.Event.ON_RESUME -> {
                logger.debug { "StyledPlayerView: ON_RESUME" }
                this.onResume()
            }

            Lifecycle.Event.ON_PAUSE -> {
                logger.debug { "StyledPlayerView: ON_PAUSE" }
                this.onPause()
            }

            Lifecycle.Event.ON_DESTROY -> {
                logger.debug { "StyledPlayerView: ON_DESTROY" }
                // handled in onDispose
            }

            else -> { /* ignore */
            }
        }
        previousState.value = event
    }