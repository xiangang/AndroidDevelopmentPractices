package com.nxg.composeplane.view

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.TargetBasedAnimation
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nxg.composeplane.R
import com.nxg.composeplane.model.GameState
import com.nxg.composeplane.model.OnGameAction
import com.nxg.composeplane.model.PLAYER_PLANE_SPRITE_SIZE
import com.nxg.composeplane.model.PlayerPlane
import com.nxg.composeplane.util.LogUtil
import kotlinx.coroutines.InternalCoroutinesApi

/**
 * 玩家飞机爆炸动画
 */
@InternalCoroutinesApi
@ExperimentalAnimationApi
@Composable
fun PlayerPlaneBombSprite(
    gameState: GameState = GameState.Waiting,
    playerPlane: PlayerPlane,
    onGameAction: OnGameAction
) {
    if (gameState != GameState.Dying) {
        return
    }
    val spriteSize = PLAYER_PLANE_SPRITE_SIZE.dp
    val spriteSizePx = with(LocalDensity.current) { spriteSize.toPx() }

    val segment = playerPlane.segment
    val anim = remember {
        TargetBasedAnimation(
            animationSpec = tween(172),
            typeConverter = Int.VectorConverter,
            initialValue = 0,
            targetValue = segment - 1
        )
    }
    var animationValue by remember {
        mutableStateOf(0)
    }
    var playTime by remember { mutableStateOf(0L) }
    LaunchedEffect(gameState) {
        val startTime = withFrameNanos { it }
        do {
            playTime = withFrameNanos { it } - startTime
            animationValue = anim.getValueFromNanos(playTime)
        } while (!anim.isFinishedFromNanos(playTime))

    }
    LogUtil.printLog(message = "PlayerPlaneBombSprite() animationValue $animationValue")

    //这里使用修改ImageBitmap.imageResource返回bitmap方便处理
    val bitmap: Bitmap = imageResource(R.drawable.sprite_player_plane_bomb_seq)
    //分割Bitmap
    val displayBitmapWidth = bitmap.width / segment

    val matrix = Matrix()
    matrix.postScale(spriteSizePx / displayBitmapWidth, spriteSizePx / bitmap.height)
    //只获取需要的部分
    val displayBitmap = Bitmap.createBitmap(
        bitmap,
        (animationValue * displayBitmapWidth),
        0,
        displayBitmapWidth,
        bitmap.height,
        matrix,
        true
    )

    val imageBitmap: ImageBitmap = displayBitmap.asImageBitmap()

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .size(spriteSize)
    ) {

        val canvasWidth = size.width
        val canvasHeight = size.height

        drawImage(
            imageBitmap,
            topLeft = Offset(
                playerPlane.x.toFloat(),
                playerPlane.y.toFloat(),
            ),
            alpha = if (gameState == GameState.Dying) 1.0f else 0f,
        )
    }

    if (animationValue == segment - 1) {
        onGameAction.onOver()
    }
}


@InternalCoroutinesApi
@ExperimentalAnimationApi
@Preview()
@Composable
fun PreviewFighterJetPlaneBombSprite() {

}
