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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.nxg.composeplane.model.*
import com.nxg.composeplane.util.LogUtil
import com.nxg.composeplane.util.SpriteUtil.isCollisionWithRect
import kotlinx.coroutines.InternalCoroutinesApi
import kotlin.math.roundToInt

/**
 * 敌军小飞机
 * 只能沿着Y轴飞行（不能沿X轴运动）
 */

@InternalCoroutinesApi
@ExperimentalAnimationApi
@Composable
fun ShowEnemyPlaneSprite(
    gameState: GameState,
    gameScore: Int,
    playerPlane: PlayerPlane,
    bulletList: List<Bullet>,
    enemyPlaneList: List<EnemyPlane>,
    onGameAction: OnGameAction
) {
    for (enemyPlane in enemyPlaneList) {
        EnemyPlaneSpriteBomb(
            gameState,
            gameScore,
            playerPlane,
            bulletList,
            enemyPlane,
            onGameAction
        )
    }
}

@InternalCoroutinesApi
@ExperimentalAnimationApi
@Composable
fun EnemyPlaneSpriteBomb(
    gameState: GameState,
    gameScore: Int,
    playerPlane: PlayerPlane,
    bulletList: List<Bullet>,
    enemyPlane: EnemyPlane,
    onGameAction: OnGameAction
) {
    //爆炸动画控制标志位
    var showBombAnim by remember {
        mutableStateOf(false)
    }

    EnemyPlaneSpriteBomb(gameScore, enemyPlane, showBombAnim,
        onBombAnimChange = {
            showBombAnim = it
        })

    EnemyPlaneSpriteFly(
        gameState,
        gameScore,
        playerPlane,
        bulletList,
        onBombAnimChange = {
            showBombAnim = it
        },
        enemyPlane,
        onGameAction
    )

}


@InternalCoroutinesApi
@ExperimentalAnimationApi
@Composable
fun EnemyPlaneSpriteFly(
    gameState: GameState,
    gameScore: Int,
    playerPlane: PlayerPlane,
    bulletList: List<Bullet>,
    onBombAnimChange: (Boolean) -> Unit,
    enemyPlane: EnemyPlane,
    onGameAction: OnGameAction
) {
    //获取屏幕宽高
    val widthPixels = LocalContext.current.resources.displayMetrics.widthPixels
    val heightPixels = LocalContext.current.resources.displayMetrics.heightPixels

    //初始化敌机的大小，速度，XY轴上的飞行范围
    val enemyPlaneWidth = enemyPlane.width
    val enemyPlaneWidthPx = with(LocalDensity.current) { enemyPlaneWidth.toPx() }
    val enemyPlaneVelocity = enemyPlane.velocity
    val maxEnemyPlaneSpriteX = widthPixels - enemyPlaneWidthPx //X轴屏幕宽度向左偏移一个机身
    val maxEnemyPlaneSpriteY = heightPixels * 1.5 //Y轴1.5倍屏幕高度

    //玩家飞机大小
    val playerPlaneSize = PLAYER_PLANE_SPRITE_SIZE.dp
    val playerPlaneSizePx = with(LocalDensity.current) { playerPlaneSize.toPx() }

    //子弹大小
    val bulletSpriteWidth = BULLET_SPRITE_WIDTH.dp
    val bulletSpriteWidthPx = with(LocalDensity.current) { bulletSpriteWidth.toPx() }

    val bulletSpriteHeight = BULLET_SPRITE_HEIGHT.dp
    val bulletSpriteHeightPx = with(LocalDensity.current) { bulletSpriteHeight.toPx() }

    //重复动画，1秒60帧，测试发现，如果不使用frame，则动画不会循环进行
    val infiniteTransition = rememberInfiniteTransition()
    val frame by infiniteTransition.animateInt(
        initialValue = 0,
        targetValue = 60,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    //同步敌机Y轴上的位置
    if (gameState == GameState.Running) {
        enemyPlane.y += enemyPlaneVelocity
    }

    //如果未初始化，则给个随机值(在屏幕范围内)
    if (!enemyPlane.init) {
        enemyPlane.x = (0..maxEnemyPlaneSpriteX.roundToInt()).random()
        enemyPlane.y = if (enemyPlane.startY >= 0) {
            (-heightPixels..0).random()
        } else {
            enemyPlane.startY + (1..3).random()
        }
        enemyPlane.init = true
        enemyPlane.reBirth()
    }

    //飞出屏幕，则死亡
    if (enemyPlane.y >= maxEnemyPlaneSpriteY) {
        enemyPlane.init = false//这里不能在die方法里调用，否则碰撞检测爆炸后，敌机的位置马上变化了
        enemyPlane.die()
    }

    LogUtil.printLog(message = "EnemyPlaneSpriteFly: state = ${enemyPlane.state}, enemyPlane.startY = ${enemyPlane.startY}，enemyPlane.x = ${enemyPlane.x}， enemyPlane.y = ${enemyPlane.y}, frame = $frame ")

    //碰撞检测逻辑
    if (gameState == GameState.Running) {

        //这里是使用了爆炸道具
        if (enemyPlane.isAlive() && enemyPlane.isNoPower()) {
            //敌机死亡
            enemyPlane.die()
            //爆炸动画可显示
            onBombAnimChange(true)
            //爆炸动画是观察分数变化来触发的(onDestroyAllEnemy触发)
            //onGameAction.onScore(gameScore + enemyPlane.value)
        }

        //如果敌机碰撞到了玩家飞机(碰撞检测要求，碰撞双方必须都在屏幕内)
        if (enemyPlane.isAlive() && playerPlane.x > 0 && playerPlane.y > 0 && enemyPlane.x > 0 && enemyPlane.y > 0 && isCollisionWithRect(
                playerPlane.x,
                playerPlane.y,
                playerPlaneSizePx.roundToInt(),
                playerPlaneSizePx.roundToInt(),
                enemyPlane.x,
                enemyPlane.y,
                enemyPlaneWidthPx.roundToInt(),
                enemyPlaneWidthPx.roundToInt()
            )
        ) {
            //玩家飞机爆炸，进入GameState.Dying状态，播放爆炸动画，动画结束后进入GameState.Over，弹出提示框，选择重新开始或退出
            if (gameState == GameState.Running) {
                if (playerPlane.isNoProtect()) {
                    onGameAction.onDying()
                }
            }

        }

        //遍历子弹和敌机是否发生碰撞
        for (bullet in bulletList) {
            //如果敌机存活且碰撞到了子弹(碰撞检测要求，碰撞双方必须都在屏幕内)
            if (enemyPlane.isAlive() && bullet.isAlive() && bullet.x > 0 && bullet.y > 0 && isCollisionWithRect(
                    bullet.x,
                    bullet.y,
                    bulletSpriteWidthPx.roundToInt(),
                    bulletSpriteHeightPx.roundToInt(),
                    enemyPlane.x,
                    enemyPlane.y,
                    enemyPlaneWidthPx.roundToInt(),
                    enemyPlaneWidthPx.roundToInt()
                )
            ) {

                bullet.die()
                enemyPlane.beHit(bullet.hit)
                //敌机无能量后就爆炸
                if (enemyPlane.isNoPower()) {
                    //敌机死亡
                    enemyPlane.die()
                    //爆炸动画可显示
                    onBombAnimChange(true)
                    //爆炸动画是观察分数变化来触发的
                    onGameAction.onScore(gameScore + enemyPlane.value)
                    break
                }
            }
        }
    }

    //被击中时更新图片
    var realDrawableId = enemyPlane.drawableIds[0]
    //如果被击中，则重新计算显示的图片index，先算生命值按图片数量平均，一张图片对应的powerSegment是多少，再用被击中消耗的生命值除以powerSegment，得到倍数
    if (enemyPlane.hit > 0) {
        val hitPerPower = enemyPlane.hit / (enemyPlane.power / enemyPlane.drawableIds.size)
        val drawableIdsIndex = when {
            hitPerPower < 0 -> {
                0
            }
            hitPerPower >= enemyPlane.drawableIds.size -> {
                enemyPlane.drawableIds.size - 1
            }
            else -> {
                hitPerPower
            }
        }
        realDrawableId = enemyPlane.drawableIds[drawableIdsIndex]
    }

    //绘制图片
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(realDrawableId),
            contentScale = ContentScale.FillBounds,
            contentDescription = null,
            modifier = Modifier
                .offset { IntOffset(enemyPlane.x, enemyPlane.y) }
                //.background(Color.Red)
                .size(enemyPlaneWidth)
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
