package com.nxg.composeplane.view


import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.nxg.composeplane.R
import com.nxg.composeplane.model.GameState
import com.nxg.composeplane.model.OnGameAction
import com.nxg.composeplane.model.PLAYER_PLANE_SPRITE_SIZE
import com.nxg.composeplane.util.LogUtil
import com.nxg.composeplane.util.ScoreFontFamily
import kotlinx.coroutines.InternalCoroutinesApi

/**
 * 游戏开始界面
 */
@ExperimentalAnimationApi
@InternalCoroutinesApi
@Composable
fun GameStart(
    gameState: GameState,
    onGameAction: OnGameAction = OnGameAction()

) {
    LogUtil.printLog(message = "GameStart()")

    val widthPixels = LocalContext.current.resources.displayMetrics.widthPixels
    val heightPixels = LocalContext.current.resources.displayMetrics.heightPixels

    Box(
        modifier = Modifier
            .wrapContentSize()
            .alpha(if (gameState == GameState.Waiting) 1.0f else 0f)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            Spacer(
                Modifier
                    .weight(1f)
            )

            Image(
                painter = painterResource(id = R.drawable.sprite_title),
                contentScale = ContentScale.FillWidth,
                contentDescription = null,
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterHorizontally)
                    .width(300.dp)
                    .wrapContentHeight()
            )

            Spacer(
                Modifier
                    .weight(1f)
            )

            TextButton(
                onClick = onGameAction.onStart,
                modifier = Modifier
                    .weight(1f)
                    .padding(20.dp)
                    .wrapContentSize()
                    .align(Alignment.CenterHorizontally)
                    .background(Color.Transparent),
                border = BorderStroke(1.dp, Color.DarkGray),
                content = {
                    Text(
                        text = "开始游戏",
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .wrapContentWidth(Alignment.End),
                        style = MaterialTheme.typography.h5,
                        color = Color.Black,
                        fontFamily = ScoreFontFamily
                    )
                }
            )

            Spacer(
                Modifier
                    .weight(1f)
            )
        }

    }

    GameStartPlaneInAndOut(gameState, widthPixels, heightPixels)

}


@InternalCoroutinesApi
@ExperimentalAnimationApi
@Composable
fun GameStartPlaneInAndOut(
    gameState: GameState,
    widthPixels: Int,
    heightPixels: Int
) {

    val playerPlaneSize = PLAYER_PLANE_SPRITE_SIZE.dp
    val playerPlaneSizePx = with(LocalDensity.current) { playerPlaneSize.toPx() }

    val startOffsetY = heightPixels + playerPlaneSizePx
    val endOffsetY = heightPixels / 2f - playerPlaneSizePx / 2f

    var offsetYIn by remember {
        mutableStateOf(startOffsetY)
    }

    var realOffsetY by remember {
        mutableStateOf(0f)
    }

    val offsetX by remember {
        mutableStateOf(widthPixels / 2f - playerPlaneSizePx / 2f)
    }

    //可以考虑rememberUpdatedState：在效应中引用某个值，该效应在值改变时不应重启
    var show by remember {
        mutableStateOf(true)
    }

    if (gameState == GameState.Over) {
        show = false
    }

    //从底部飞入动画
    val animateInState by remember { mutableStateOf(0) }

    var playTimeIn by remember { mutableStateOf(0L) }
    val animIn = remember {
        TargetBasedAnimation(
            animationSpec = tween(
                durationMillis = 500,
                delayMillis = 200,
                easing = FastOutSlowInEasing
            ),
            typeConverter = Float.VectorConverter,
            initialValue = startOffsetY,
            targetValue = endOffsetY
        )
    }
    LaunchedEffect(animateInState) {
        val startTime = withFrameNanos { it }
        do {
            playTimeIn = withFrameNanos { it } - startTime
            offsetYIn = animIn.getValueFromNanos(playTimeIn)
        } while (!animIn.isFinishedFromNanos(playTimeIn))

    }

    //原地喷气动画
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(SMALL_ENEMY_PLANE_SPRITE_ALPHA, easing = FastShowAndHiddenEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    //向顶部飞出动画
    var offsetYOut by remember {
        mutableStateOf(endOffsetY)//飞出的起点，是飞入的终点
    }
    var playTimeOut by remember { mutableStateOf(0L) }
    val animOut = remember {
        TargetBasedAnimation(
            animationSpec = tween(
                durationMillis = 300,
                delayMillis = 0,
                easing = LinearEasing
            ),
            typeConverter = Float.VectorConverter,
            initialValue = endOffsetY,
            targetValue = -playerPlaneSizePx * 2f
        )
    }

    LaunchedEffect(gameState) {
        val startTime = withFrameNanos { it }
        do {
            playTimeOut = withFrameNanos { it } - startTime
            offsetYOut = animOut.getValueFromNanos(playTimeOut)
        } while (!animOut.isFinishedFromNanos(playTimeOut))

    }

    LogUtil.printLog(message = "GameStart() offsetYIn $offsetYIn, offsetYOut $offsetYOut，offsetX $offsetX, alpha $alpha")

    //ImageBitmap.imageResource(id = R.drawable.sprite_loading_1)
    LogUtil.printLog(message = "GameStart() before realOffsetY $realOffsetY")

    if (gameState == GameState.Waiting) {
        realOffsetY = offsetYIn
    }
    if (gameState == GameState.Running) {
        realOffsetY = offsetYOut
    }

    LogUtil.printLog(message = "GameStart() after realOffsetY $realOffsetY")

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.sprite_player_plane_1),
            contentScale = ContentScale.FillBounds,
            contentDescription = null,
            modifier = Modifier
                .offset { IntOffset(offsetX.toInt(), realOffsetY.toInt()) }
                .size(playerPlaneSize),
            alpha = if (show) {
                if (offsetYIn >= endOffsetY) {
                    if (alpha < 0.5f) 0f else 1f
                } else {
                    1f
                }
            } else {
                0f
            }
        )

        //显示另一张飞机喷气图，通过循环设置相反的alpha，达到动态喷气的效果
        Image(
            painter = painterResource(id = R.drawable.sprite_player_plane_2),
            contentScale = ContentScale.FillBounds,
            contentDescription = null,
            modifier = Modifier
                .offset { IntOffset(offsetX.toInt(), realOffsetY.toInt()) }
                .size(playerPlaneSize),
            alpha = if (offsetYIn >= endOffsetY && show) {
                if (1 - alpha < 0.5f) 0f else 1f
            } else {
                0f
            }
        )
    }


}


@InternalCoroutinesApi
@ExperimentalAnimationApi
@Preview()
@Composable
fun PreviewGameStart() {
    FarBackground()
    GameStart(
        GameState.Waiting,
        OnGameAction()
    )
}

