package com.nxg.composeplane.view

import android.annotation.SuppressLint
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.nxg.composeplane.model.*
import com.nxg.composeplane.util.LogUtil
import com.nxg.composeplane.util.ScoreFontFamily
import com.nxg.composeplane.util.SpriteUtil
import kotlinx.coroutines.InternalCoroutinesApi
import kotlin.math.roundToInt

/**
 * 奖励精灵显示
 */
@SuppressLint("RememberReturnType")
@InternalCoroutinesApi
@Composable
fun ComposeAwardSprite(
    gameState: GameState,
    playerPlane: PlayerPlane,
    awardList: List<Award>,
    onGameAction: OnGameAction = OnGameAction()

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

    for ((index, award) in awardList.withIndex()) {
        LogUtil.printLog(message = "ComposeAwardSprite()---> award.isAlive() = ${award.isAlive()}")
        if (award.isAlive()) {
            ComposeAwardSpriteFall(gameState, playerPlane, award, index, onGameAction)
        }
    }

    LogUtil.printLog(message = "ComposeAwardSprite()---> frame = $frame, awardList.size = ${awardList.size}")

}


/**
 * 奖励精灵下落
 */
@InternalCoroutinesApi
@Composable
fun ComposeAwardSpriteFall(
    gameState: GameState = GameState.Waiting,
    playerPlane: PlayerPlane,
    award: Award,
    index: Int,
    onGameAction: OnGameAction = OnGameAction()

) {
    LogUtil.printLog(message = "ComposeAwardSpriteFall() ---> award = ${award}")
    //准备工作
    val widthPixels = LocalContext.current.resources.displayMetrics.widthPixels
    val heightPixels = LocalContext.current.resources.displayMetrics.heightPixels
    val playerPlaneSize = PLAYER_PLANE_SPRITE_SIZE.dp
    val playerPlaneSizePx = with(LocalDensity.current) { playerPlaneSize.toPx() }
    val awardWidth = award.width
    val awardWidthPx = with(LocalDensity.current) { awardWidth.toPx() }
    val awardHeight = award.height
    val awardHeightPx = with(LocalDensity.current) { awardHeight.toPx() }
    val maxAwardSpriteX = widthPixels - awardWidthPx //X轴屏幕宽度向左偏移一个身位
    val maxAwardSpriteY = heightPixels * 1.5 //屏幕高度

    //游戏进行中
    if (gameState == GameState.Running) {

        //初始化起点
        if (!award.init) {
            award.startX = (0..maxAwardSpriteX.roundToInt()).random()
            award.startY = -awardHeightPx.toInt()
            award.x = award.startX
            award.y = award.startY
            LogUtil.printLog(message = "ComposeAwardFall() init start x,y  ---> index = $index, award.state = ${award.state}, award.startY = ${award.startY}")
            award.init = true
        }

        //飞行指定的距离(这里是一个屏幕高度的距离)后
        if (award.isAlive() && award.y >= maxAwardSpriteY) {
            LogUtil.printLog(message = "ComposeAwardFall() die  ---> index = $index, award.state = ${award.state}, award.startY = ${award.startY}")
            award.die()
        }

        //下落
        award.x = award.startX
        award.y += award.velocity

        LogUtil.printLog(message = "ComposeAwardFall() falling --->  index = $index, award.state = ${award.state}, award.startY = ${award.startY},  award.y = ${award.y}")

        //如果道具奖励碰撞到了玩家飞机(碰撞检测要求，碰撞双方必须都在屏幕内)
        if (playerPlane.isAlive() && playerPlane.x > 0 && playerPlane.y > 0 && award.isAlive() && award.x > 0 && award.y > 0 && SpriteUtil.isCollisionWithRect(
                playerPlane.x,
                playerPlane.y,
                playerPlaneSizePx.roundToInt(),
                playerPlaneSizePx.roundToInt(),
                award.x,
                award.y,
                awardWidthPx.roundToInt(),
                awardHeightPx.roundToInt()
            )
        ) {
            onGameAction.onAward(award)
            award.die()
        }

    }

    //绘制图片
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = award.drawableId),
            contentScale = ContentScale.FillBounds,
            contentDescription = null,
            modifier = Modifier
                .offset {
                    IntOffset(
                        award.x,
                        award.y
                    )
                }
                .width(award.width)
                .height(award.height)
                .alpha(
                    if (gameState == GameState.Running) {
                        if (award.isDead()) 0f else 1f
                    } else {
                        0f
                    }
                )
        )
    }

}

@InternalCoroutinesApi
@Preview()
@Composable
fun PreviewComposeAward() {

}


/**
 * 奖励
 */
@InternalCoroutinesApi
@Composable
fun ComposeBombAward(
    playerPlane: PlayerPlane = PlayerPlane(bombAward = (0 shl 16 or 100)),
    gameAction: OnGameAction = OnGameAction()
) {
    LogUtil.printLog(message = "ComposeBombAward()")
    //初始化必要的参数
    val bombAward = playerPlane.bombAward
    val bombNum = bombAward and 0xFFFF //数量
    val bombWidth = 44.dp
    val bombHeight = 40.dp
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Column(
            Modifier
                .fillMaxSize()
                .padding(top = 20.dp)
                .alpha(if (bombNum > 0) 1f else 0f)
        ) {
            Spacer(
                modifier = Modifier
                    .weight(1f)
            )
            Row(
                modifier = Modifier
                    .padding(10.dp)
                    .wrapContentSize()
                //.offset { IntOffset(bombWidthPx.roundToInt(), offsetY.roundToInt()) }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.sprite_red_bomb),
                    contentScale = ContentScale.FillBounds,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .size(bombWidth, bombHeight)
                        .clickable(onClick = gameAction.onDestroyAllEnemy)
                )

                Text(
                    text = " x $bombNum",
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .align(Alignment.CenterVertically)
                        .wrapContentWidth(Alignment.End),
                    style = MaterialTheme.typography.h4,
                    color = Color.Black,
                    fontFamily = ScoreFontFamily
                )

            }
        }

    }

}

@InternalCoroutinesApi
@Preview()
@Composable
fun PreviewComposeBombAward() {
    ComposeBombAward()
}
