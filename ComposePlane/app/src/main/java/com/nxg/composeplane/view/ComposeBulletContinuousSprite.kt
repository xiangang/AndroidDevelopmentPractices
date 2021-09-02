package com.nxg.composeplane.view

/**
 * 子弹
 */
import android.annotation.SuppressLint
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nxg.composeplane.model.*
import com.nxg.composeplane.util.LogUtil
import com.nxg.composeplane.viewmodel.GameViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.roundToInt

/**
 * 暂时没用到
 * 子弹从玩家飞机顶部发射，只能沿着X轴运动，超出屏幕则销毁，与敌机碰撞也销毁，同时计算得分
 * 支持连续射击，每次射击一个弹夹的子弹，射击完后，更换弹夹
 */
@SuppressLint("RememberReturnType")
@InternalCoroutinesApi
@Composable
fun BulletContinuousSprite(
    gameState: GameState = GameState.Waiting,
    playerPlane: PlayerPlane,
    bulletList: List<Bullet>,
    onGameAction: OnGameAction = OnGameAction()
) {
    LogUtil.printLog(message = "BulletContinuousSprite()--->")


    //重复动画，1秒60帧
    val infiniteTransition = rememberInfiniteTransition()
    val frame by infiniteTransition.animateInt(
        initialValue = 0,
        targetValue = 60,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    /* if (frame % 7 == 0) {
         bulletList.add(Bullet(x = 500, y = 1080))
     }*/

    val lastBulletY = bulletList[bulletList.size - 1]

    for ((index, bullet) in bulletList.withIndex()) {

        val bulletHeight = bullet.height
        val bulletHeightPx = with(LocalDensity.current) { bulletHeight.toPx() }

        if (bullet.x <= 0) {
            //bullet.x = (100..800).random()
            bullet.x = bullet.startX
        }
        //当最后一颗子弹飞出去时
        if (bullet.y <= 0) {
            val startY = (bullet.startY + index * 2 * bulletHeightPx).toInt()
            LogUtil.printLog(message = "BulletContinuousSprite()---> startY = $startY, index = $index")
            bullet.y = (bullet.startY + index * 2 * bulletHeightPx).toInt()
        }

        //bullet.y -= (2 * bulletHeightPx).toInt()
        bullet.y -= 20

        LogUtil.printLog(message = "BulletContinuousSprite()---> frame = $frame, index = $index, bulletList.size = ${bulletList.size}, bullet.x = ${bullet.x},  bullet.y = ${bullet.y}")

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
                    .background(
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
                    )
                    .width(bullet.width)
                    .height(bullet.height)
                /*.alpha(
                     if (gameState == GameState.Running) {
                         if (bullet.isDead()) 0f else 1f
                     } else {
                         0f
                     }
                 )*/
            )
        }
    }


}


/**
 * 射击子弹
 */
@SuppressLint("RememberReturnType")
@InternalCoroutinesApi
@Composable
fun BulletContinuous(
    gameViewModel: GameViewModel = viewModel(),
    gameState: GameState = GameState.Waiting,
    bullet: Bullet

) {
    LogUtil.printLog(message = "BulletContinuous()--->")
    bullet.y -= 10
    LogUtil.printLog(message = "BulletContinuousSprite()---> bullet.x ${bullet.x}  bullet.y ${bullet.y}")
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
            /*.alpha(
                 if (gameState == GameState.Running) {
                     if (bullet.isDead()) 0f else 1f
                 } else {
                     0f
                 }
             )*/
        )
    }
    /* if (bullet.y <= 0) {
         bulletList.remove(bullet)
         continue
     }*/
}


/**
 * 射击子弹
 */
@SuppressLint("RememberReturnType")
@InternalCoroutinesApi
@Composable
fun ShootingBullets(
    gameViewModel: GameViewModel = viewModel(),
    gameState: GameState = GameState.Waiting,
    bullet: Bullet

) {
    LogUtil.printLog(message = "ShootingBullets()--->")

    if (gameState == GameState.Dying || gameState == GameState.Over) {
        return
    }


    //获取所有子弹
    val bulletList by gameViewModel.bulletListFlow.collectAsState()


}

@Preview()
@Composable
fun PreviewBulletContinuousSprite() {

}

