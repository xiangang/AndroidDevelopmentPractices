package com.nxg.composeplane.view

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import com.nxg.composeplane.R
import com.nxg.composeplane.model.GameState
import com.nxg.composeplane.model.GameAction
import com.nxg.composeplane.model.PlayerPlane
import com.nxg.composeplane.util.LogUtil
import kotlinx.coroutines.InternalCoroutinesApi
import kotlin.math.roundToInt

/**
 * 玩家飞机，可手指拖动，沿XY轴同时移动
 */
val FastShowAndHiddenEasing: Easing = CubicBezierEasing(0.0f, 0.0f, 1.0f, 1.0f)//喷气速度变化
const val SMALL_ENEMY_PLANE_SPRITE_ALPHA = 100; //喷气速度

@InternalCoroutinesApi
@ExperimentalAnimationApi
@Composable
fun PlayerPlaneSprite(
    gameState: GameState,
    playerPlane: PlayerPlane,
    gameAction: GameAction
) {
    if (!(gameState == GameState.Running || gameState == GameState.Paused)) {
        return
    }

    //初始化参数
    val widthPixels = LocalContext.current.resources.displayMetrics.widthPixels
    val heightPixels = LocalContext.current.resources.displayMetrics.heightPixels
    val playerPlaneHeightPx = with(LocalDensity.current) { playerPlane.height.toPx() }

    //循环动画
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(SMALL_ENEMY_PLANE_SPRITE_ALPHA, easing = FastShowAndHiddenEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    //游戏开始后，动画完成减少保护次数，直到为0
    if (gameState == GameState.Running && !playerPlane.isNoProtect() && alpha >= 0.5f) {
        playerPlane.reduceProtect()
    }

    LogUtil.printLog(message = "PlayerPlaneSprite() playerPlane.x = ${playerPlane.x}  playerPlane.y = ${playerPlane.y}")
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.sprite_player_plane_1),
            contentScale = ContentScale.FillBounds,
            contentDescription = null,
            modifier = Modifier
                .offset { IntOffset(playerPlane.x, playerPlane.y) }
                //.background(Color.Blue)
                .size(playerPlane.width, playerPlane.height)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consumeAllChanges()
                        //这里有点奇怪，打印的状态没有跟着变化，可能是作用域的问题？
                        LogUtil.printLog(message = "PlayerPlaneSprite() detectDragGestures gameState =  $gameState")

                        var newOffsetX = playerPlane.x
                        var newOffsetY = playerPlane.y
                        //边界检测
                        when {
                            newOffsetX + dragAmount.x <= 0 -> {
                                newOffsetX = 0
                            }
                            (newOffsetX + dragAmount.x + playerPlaneHeightPx) >= widthPixels -> {
                                widthPixels.let {
                                    newOffsetX = it - playerPlaneHeightPx.roundToInt()
                                }
                            }
                            else -> {
                                newOffsetX += dragAmount.x.roundToInt()
                            }
                        }
                        when {
                            newOffsetY + dragAmount.y <= 0 -> {
                                newOffsetY = 0
                            }
                            (newOffsetY + dragAmount.y) >= heightPixels -> {
                                heightPixels.let {
                                    newOffsetY = it
                                }
                            }
                            else -> {
                                newOffsetY += dragAmount.y.roundToInt()
                            }
                        }
                        gameAction.playerMove(newOffsetX, newOffsetY)
                    }
                }
                .alpha(
                    if (gameState == GameState.Running || gameState == GameState.Paused) {
                        if (alpha < 0.5f) 0f else 1f
                    } else {
                        0f
                    }
                )
        )

        //显示另一张飞机喷气图，通过循环设置相反的alpha，达到动态喷气的效果
        Image(
            painter = painterResource(id = R.drawable.sprite_player_plane_2),
            contentScale = ContentScale.FillBounds,
            contentDescription = null,
            modifier = Modifier
                .offset { IntOffset(playerPlane.x, playerPlane.y) }
                //.background(Color.Blue)
                .size(playerPlane.width, playerPlane.height)
                .alpha(
                    if (gameState == GameState.Running || gameState == GameState.Paused) {
                        //如果处于保护状态这里就不显示了
                        if (!playerPlane.isNoProtect()) {
                            0f
                        } else {
                            if (1 - alpha < 0.5f) 0f else 1f
                        }
                    } else {
                        0f
                    }
                )
        )
    }
}

/**
 * PlayerPlaneAnimIn
 * 飞入动画
 */
@InternalCoroutinesApi
@ExperimentalAnimationApi
@Composable
fun PlayerPlaneAnimIn(
    gameState: GameState,
    playerPlane: PlayerPlane,
    gameAction: GameAction = GameAction()
) {
    if (gameState != GameState.Running) {
        return
    }

    LogUtil.printLog(message = "PlayerPlaneAnimIn() playerPlane.animateIn = ${playerPlane.animateIn}")

    //初始化必要的参数
    val heightPixels = LocalContext.current.resources.displayMetrics.heightPixels
    val playerPlaneHeightPx = with(LocalDensity.current) { playerPlane.height.toPx() }
    val startOffsetY = playerPlane.startY.toFloat()
    val endOffsetY = heightPixels - playerPlaneHeightPx * 1.5f
    val realOffsetX by remember { mutableStateOf(playerPlane.startX.toFloat()) }
    var realOffsetY by remember { mutableStateOf(playerPlane.startY.toFloat()) }

    //从底部飞入动画
    var animInState by remember { mutableStateOf(false) }
    var show by remember { mutableStateOf(false) }//默认false不显示动画
    var offsetYIn by remember {
        mutableStateOf(startOffsetY)
    }
    var playTimeIn by remember { mutableStateOf(0L) }
    val animIn = remember {
        TargetBasedAnimation(
            animationSpec = tween(
                durationMillis = 300,
                delayMillis = 0,
                easing = LinearOutSlowInEasing
            ),
            typeConverter = Float.VectorConverter,
            initialValue = startOffsetY,
            targetValue = endOffsetY
        )
    }
    LaunchedEffect(animInState) {
        val startTime = withFrameNanos { it }
        do {
            playTimeIn = withFrameNanos { it } - startTime
            offsetYIn = animIn.getValueFromNanos(playTimeIn)
        } while (!animIn.isFinishedFromNanos(playTimeIn))

    }
    LogUtil.printLog(message = "PlayerPlaneAnimIn() before animInState = $animInState")

    if (!playerPlane.animateIn) {
        return
    }

    //运行后修改状态，重新执行一次动画
    if (gameState == GameState.Running && !show && !animInState) {
        offsetYIn = startOffsetY
        show = true
        animInState = true
    }

    //死亡后，重置
    if (gameState == GameState.Dying && !show) {
        offsetYIn = startOffsetY
        realOffsetY = startOffsetY
        animInState = false
    }
    LogUtil.printLog(message = "PlayerPlaneAnimIn() after animInState = $animInState")

    //如果需要显示
    if (show) {
        realOffsetY = offsetYIn
    }

    //动画执行完毕不再显示
    if (show && offsetYIn <= endOffsetY) {
        show = false
        //此时更新飞机真正的位置
        gameAction.playerMove(realOffsetX.roundToInt(), realOffsetY.roundToInt())
    }
    LogUtil.printLog(message = "PlayerPlaneAnimIn() offsetYIn2 = $offsetYIn, realOffsetY = $realOffsetY, show $show ")

    //喷气动画
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(SMALL_ENEMY_PLANE_SPRITE_ALPHA, easing = FastShowAndHiddenEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.sprite_player_plane_1),
            contentScale = ContentScale.FillBounds,
            contentDescription = null,
            modifier = Modifier
                .offset { IntOffset(realOffsetX.roundToInt(), realOffsetY.roundToInt()) }
                .size(playerPlane.width, playerPlane.height)
                //.background(Color.Green)
                .alpha(
                    if (show) {
                        if (alpha < 0.5f) 0f else 1f
                    } else {
                        0f
                    }
                )
        )

        //显示另一张飞机喷气图，通过循环设置相反的alpha，达到动态喷气的效果
        Image(
            painter = painterResource(id = R.drawable.sprite_player_plane_2),
            contentScale = ContentScale.FillBounds,
            contentDescription = null,
            modifier = Modifier
                .offset { IntOffset(realOffsetX.roundToInt(), realOffsetY.roundToInt()) }
                .size(playerPlane.width, playerPlane.height)
                //.background(Color.Green)
                .alpha(
                    if (show) {
                        if (1 - alpha < 0.5f) 0f else 1f
                    } else {
                        0f
                    }
                )
        )
    }
}


@InternalCoroutinesApi
@ExperimentalAnimationApi
@Preview()
@Composable
fun PreviewFighterJetPlaneSprite() {
    PlayerPlaneSprite(
        GameState.Waiting,
        PlayerPlane(x = 480, y = 1900),
        GameAction()
    )
}
