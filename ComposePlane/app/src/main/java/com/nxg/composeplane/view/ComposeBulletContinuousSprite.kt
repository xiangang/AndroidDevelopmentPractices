package com.nxg.composeplane.view

/**
 * 子弹
 */
import android.annotation.SuppressLint
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nxg.composeplane.model.Bullet
import com.nxg.composeplane.model.GameState
import com.nxg.composeplane.model.PLAYER_PLANE_SPRITE_SIZE
import com.nxg.composeplane.model.SpriteState
import com.nxg.composeplane.util.LogUtil
import com.nxg.composeplane.viewmodel.GameViewModel
import kotlinx.coroutines.InternalCoroutinesApi
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
    gameViewModel: GameViewModel = viewModel(),
    gameState: GameState = GameState.Waiting

) {
    LogUtil.printLog(message = "BulletSprite()--->")

    if (gameState == GameState.Dying || gameState == GameState.Over) {
        return
    }


    //获取所有子弹
    val bulletList by gameViewModel.bulletListFlow.collectAsState()



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
    LogUtil.printLog(message = "BulletSprite()--->")

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

