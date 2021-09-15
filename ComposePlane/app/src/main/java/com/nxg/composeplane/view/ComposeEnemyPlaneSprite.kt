package com.nxg.composeplane.view

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import com.nxg.composeplane.model.EnemyPlane
import com.nxg.composeplane.model.GameAction
import com.nxg.composeplane.model.GameState
import com.nxg.composeplane.util.LogUtil
import kotlinx.coroutines.InternalCoroutinesApi

/**
 * 敌机
 * 只能沿着Y轴飞行（不能沿X轴运动）
 */

@InternalCoroutinesApi
@ExperimentalAnimationApi
@Composable
fun EnemyPlaneSprite(
    gameState: GameState,
    gameScore: Int,
    enemyPlaneList: List<EnemyPlane>,
    gameAction: GameAction
) {
    for (enemyPlane in enemyPlaneList) {
        EnemyPlaneSpriteBombAndFly(
            gameState,
            gameScore,
            enemyPlane,
            gameAction
        )
    }
}

@InternalCoroutinesApi
@ExperimentalAnimationApi
@Composable
fun EnemyPlaneSpriteBombAndFly(
    gameState: GameState,
    gameScore: Int,
    enemyPlane: EnemyPlane,
    gameAction: GameAction
) {
    //爆炸动画控制标志位，每个敌机都有一个独立的标志位，方便观察，不能放到EnemyPlane，因为观察不到，或者说
    var showBombAnim by remember {
        mutableStateOf(false)
    }

    EnemyPlaneSpriteMove(
        gameState,
        onBombAnimChange = {
            showBombAnim = it
        },
        enemyPlane,
        gameAction
    )

    EnemyPlaneSpriteBomb(gameScore, enemyPlane, showBombAnim,
        onBombAnimChange = {
            showBombAnim = it
        })

}


@InternalCoroutinesApi
@ExperimentalAnimationApi
@Composable
fun EnemyPlaneSpriteMove(
    gameState: GameState,
    onBombAnimChange: (Boolean) -> Unit,
    enemyPlane: EnemyPlane,
    gameAction: GameAction
) {
    //重复动画，1秒60帧(很奇怪，测试发现，如果不使用frame，则动画不会循环进行)
    val infiniteTransition = rememberInfiniteTransition()
    val frame by infiniteTransition.animateInt(
        initialValue = 0,
        targetValue = 60,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    //游戏不在进行中
    if (gameState != GameState.Running) {
        return
    }

    //敌机初始化
    gameAction.moveEnemyPlane(enemyPlane)

    LogUtil.printLog(message = "EnemyPlaneSpriteFly: state = ${enemyPlane.state}，enemyPlane.x = ${enemyPlane.x}， enemyPlane.y = ${enemyPlane.y}, frame = $frame ")

    //碰撞检测
    gameAction.collisionDetect(enemyPlane, onBombAnimChange)

    //绘制
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(enemyPlane.getRealDrawableId()),
            contentScale = ContentScale.FillBounds,
            contentDescription = null,
            modifier = Modifier
                .offset { IntOffset(enemyPlane.x, enemyPlane.y) }
                //.background(Color.Red)
                .size(enemyPlane.width)
                .alpha(if (enemyPlane.isAlive()) 1f else 0f)
        )
    }

}

@InternalCoroutinesApi
@ExperimentalAnimationApi
@Preview()
@Composable
fun PreviewEnemyPlaneSprite() {

}
