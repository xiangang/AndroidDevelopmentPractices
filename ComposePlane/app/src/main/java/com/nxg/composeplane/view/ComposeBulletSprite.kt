package com.nxg.composeplane.view

/**
 * 子弹
 */
import android.annotation.SuppressLint
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.nxg.composeplane.R
import com.nxg.composeplane.model.*
import com.nxg.composeplane.util.LogUtil
import kotlinx.coroutines.InternalCoroutinesApi
import kotlin.math.roundToInt

/**
 * 子弹从玩家飞机顶部发射，只能沿着X轴运动，超出屏幕则销毁，与敌机碰撞也销毁，同时计算得分
 */
@SuppressLint("RememberReturnType")
@InternalCoroutinesApi
@Composable
fun BulletSprite(
    gameState: GameState = GameState.Waiting,
    playerPlane: PlayerPlane,
    bulletList: List<Bullet>,
    gameAction: GameAction = GameAction()
) {
    //重复动画，1秒60帧
    val infiniteTransition = rememberInfiniteTransition()
    val frame by infiniteTransition.animateInt(
        initialValue = 0,
        targetValue = 60,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )

    for ((index, bullet) in bulletList.withIndex()) {

        if (bullet.isAlive()) {
            BulletShootingSprite(gameState, playerPlane, bullet, index, gameAction)
        }

    }

    LogUtil.printLog(message = "BulletSprite()---> frame = $frame, bulletList.size = ${bulletList.size}")

}

@SuppressLint("RememberReturnType")
@InternalCoroutinesApi
@Composable
fun BulletShootingSprite(
    gameState: GameState = GameState.Waiting,
    playerPlane: PlayerPlane,
    bullet: Bullet,
    index: Int,
    gameAction: GameAction = GameAction()

) {

    //准备工作
    val heightPixels = LocalContext.current.resources.displayMetrics.heightPixels
    val playerPlaneSize = PLAYER_PLANE_SPRITE_SIZE.dp
    val playerPlaneSizePx = with(LocalDensity.current) { playerPlaneSize.toPx() }
    val bulletWidth = bullet.width
    val bulletWidthPx = with(LocalDensity.current) { bulletWidth.toPx() }
    val bulletHeight = bullet.height
    val bulletHeightPx = with(LocalDensity.current) { bulletHeight.toPx() }

    //LogUtil.printLog(message = "BulletShootingSprite()---> frame = $frame, playerPlane.x = ${playerPlane.x}, playerPlane.y = ${playerPlane.y}")

    //游戏进行中
    if (gameState == GameState.Running) {
        //初始化起点
        if (!bullet.init) {
            bullet.startX =
                (playerPlane.x + playerPlaneSizePx / 2 - bulletWidthPx / 2).roundToInt()
            bullet.startY =
                (playerPlane.y - bulletHeightPx).roundToInt()
            bullet.x = bullet.startX
            bullet.y = bullet.startY
            //播放音频要放到IO线程
            gameAction.shooting(R.raw.shoot)
            LogUtil.printLog(message = "BulletShootingSprite() init start x,y  ---> index = $index, bullet.state = ${bullet.state}, bullet.startY = ${bullet.startY}")
            bullet.init = true
        }

        //子弹飞行指定的距离(这里是一个屏幕高度的距离)后
        if (bullet.isAlive() && bullet.startY - bullet.y >= heightPixels) {
            LogUtil.printLog(message = "BulletShootingSprite() die  ---> index = $index, bullet.state = ${bullet.state}, bullet.startY = ${bullet.startY}")
            bullet.die()
        }

        bullet.x = bullet.startX
        bullet.y -= bullet.velocity
    }

    LogUtil.printLog(message = "BulletShootingSprite() shooting --->  index = $index, bullet.state = ${bullet.state}, bullet.startY = ${bullet.startY},  bullet.y = ${bullet.y}")

    //绘制图片
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = bullet.drawableId),
            contentScale = ContentScale.FillBounds,
            contentDescription = null,
            modifier = Modifier
                .offset {
                    IntOffset(
                        bullet.x,
                        bullet.y
                    )
                }
                .width(bullet.width)
                .height(bullet.height)
                /*.background(
                    when (index) {
                        0 -> {
                            Color.Red
                        }
                        1 -> {
                            Color.Yellow
                        }
                        else -> {
                            Color.Blue
                        }
                    }
                )*/
                .alpha(
                    if (bullet.isAlive()) {
                        1f
                    } else {
                        0f
                    }
                )
        )
    }
}

@Preview()
@Composable
fun PreviewBulletSprite() {

}

