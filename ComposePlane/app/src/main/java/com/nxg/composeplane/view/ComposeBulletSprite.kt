package com.nxg.composeplane.view

/**
 * 子弹
 */
import android.annotation.SuppressLint
import android.app.Application
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.nxg.composeplane.util.SoundPoolUtil
import kotlinx.coroutines.*
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
    onGameAction: OnGameAction = OnGameAction()

) {
    if (gameState == GameState.Dying || gameState == GameState.Over) {
        return
    }

    val widthPixels = LocalContext.current.resources.displayMetrics.widthPixels
    val heightPixels = LocalContext.current.resources.displayMetrics.heightPixels

    //获取所有子弹
    val bullet = bulletList[0]

    //发射子弹动画
    val infiniteTransition = rememberInfiniteTransition()

    val playerPlaneSize = PLAYER_PLANE_SPRITE_SIZE.dp
    val playerPlaneSizePx = with(LocalDensity.current) { playerPlaneSize.toPx() }
    val bulletWidth = bullet.width
    val bulletWidthPx = with(LocalDensity.current) { bulletWidth.toPx() }
    val bulletHeight = bullet.height
    val bulletHeightPx = with(LocalDensity.current) { bulletHeight.toPx() }

    //记录子弹出现时跟玩家飞机的偏移量
    LogUtil.printLog(message = "BulletSprite() playerPlane.x = ${playerPlane.x}, playerPlane.y = ${playerPlane.y}, bullet.offsetX =  ${bullet.offsetX}, bullet.offsetY = ${bullet.offsetY}, state = ${bullet.state}")

    if (gameState == GameState.Running && playerPlane.y <= heightPixels && bullet.offsetX <= 0) {
        LogUtil.printLog(message = "BulletSprite() init --------------> ")
        bullet.offsetX =
            (playerPlane.x + playerPlaneSizePx / 2 - bulletWidthPx / 2).roundToInt()
        bullet.offsetY = (playerPlane.y - bulletHeightPx).roundToInt()
        bullet.reBirth() //复活，重新使用
        LogUtil.printLog(message = "BulletSprite() reset ----------------> bullet.offsetX ${bullet.offsetX} bullet.offsetY ${bullet.offsetY} state ${bullet.state}")

        if (gameState == GameState.Running) {
            //播放音频要放到IO线程
            onGameAction.onShooting(R.raw.shoot)
            LaunchedEffect(key1 = Unit) {
                withContext(Dispatchers.IO) {
                    //这里通过LocalContext获取不到上下文
                }
            }

        }
    }

    //根据飞行时间和飞行路程（这里飞一个屏幕高度的路程），计算每次偏移后Y轴的值
    val offsetY by infiniteTransition.animateInt(
        initialValue = 0,
        targetValue = heightPixels,
        animationSpec = infiniteRepeatable(
            animation = tween(bullet.speed, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    LogUtil.printLog(message = "BulletSprite() bullet.offsetX ${bullet.offsetX}, bullet.offsetY ${bullet.offsetY}, offsetY $offsetY heightPixels $heightPixels result ${bullet.offsetY - offsetY}")

    //飞出屏幕或飞行到终点，则重新设置子弹偏移量
    if (offsetY != 0 && bullet.offsetY != 0 && (bullet.offsetY - offsetY < 0 || offsetY >= heightPixels)) {
        bullet.offsetX = -playerPlaneSizePx.roundToInt()
        LogUtil.printLog(message = "BulletSprite() updateBullet bullet.offsetX ${bullet.offsetX} bullet.offsetY ${bullet.offsetY} state ${bullet.state}")
    }

    //实时更新子弹位置
    if (gameState == GameState.Waiting) {
        bullet.x = (playerPlane.x + playerPlaneSizePx / 2 - bulletWidthPx / 2).roundToInt()
        bullet.y = playerPlane.y
    }

    if (gameState == GameState.Running) {
        bullet.x = bullet.offsetX
        bullet.y = playerPlane.y - offsetY
    }

    LogUtil.printLog(message = "BulletSprite() bullet.state ${bullet.state} ---------------------------------------->")

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
                .alpha(
                    if (gameState == GameState.Running) {
                        if (bullet.isDead()) 0f else 1f
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

