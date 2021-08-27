package com.nxg.composeplane.view

import android.view.MotionEvent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nxg.composeplane.model.OnGameAction
import com.nxg.composeplane.util.LogUtil
import com.nxg.composeplane.viewmodel.GameViewModel
import kotlinx.coroutines.InternalCoroutinesApi

/**
 * 舞台
 */
@InternalCoroutinesApi
@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@Composable
fun Stage(gameViewModel: GameViewModel, onGameAction: OnGameAction = OnGameAction()) {

    LogUtil.printLog(message = "Stage -------> ")

    //状态提升到这里,介绍见官方文档：https://developer.android.google.cn/jetpack/compose/state#state-hoisting
    //这里主要是方便统一管理，也避免直接使用ViewModel导致无法预览（预览时viewModel()会报错）

    //获取游戏状态
    val gameState by gameViewModel.gameStateFlow.collectAsState()

    //获取游戏分数
    val gameScore by gameViewModel.gameScore.observeAsState(0)

    //获取玩家飞机
    val playerPlane by gameViewModel.playerPlaneFlow.collectAsState()

    //获取所有子弹
    val bulletList by gameViewModel.bulletListFlow.collectAsState()

    //获取所有敌军
    val enemyPlaneList by gameViewModel.enemyPlaneListFlow.collectAsState()

    //获取所有爆炸动画
    val bombList by gameViewModel.bombListFlow.collectAsState()


    val modifier = Modifier.fillMaxSize()

    Box(modifier = modifier
        .run {
            pointerInteropFilter {
                when (it.action) {
                    MotionEvent.ACTION_DOWN -> {
                        LogUtil.printLog(message = "GameScreen ACTION_DOWN ")
                    }

                    MotionEvent.ACTION_MOVE -> {
                        LogUtil.printLog(message = "GameScreen ACTION_MOVE")
                        return@pointerInteropFilter false
                    }

                    MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                        LogUtil.printLog(message = "GameScreen ACTION_CANCEL/UP")
                        return@pointerInteropFilter false
                    }
                }
                false
            }
        }) {

        // 远景
        FarBackground(modifier)

        //游戏开始界面
        GameStart(gameState, onGameAction)

        //玩家飞机
        PlayerPlaneSprite(
            gameState,
            playerPlane,
            onGameAction
        )

        //玩家飞机出场飞入动画
        PlayerPlaneAnimIn(
            gameState,
            playerPlane,
            onGameAction
        )

        //玩家飞机爆炸动画
        PlayerPlaneBombSprite(gameState, playerPlane, onGameAction)

        //子弹
        BulletSprite(gameState, playerPlane, bulletList, onGameAction)

        //敌军飞机
        ShowEnemyPlaneSprite(
            gameState,
            gameScore,
            playerPlane,
            bulletList,
            bombList,
            enemyPlaneList,
            onGameAction
        )

        //敌军飞机爆炸动画
        ComposeEnemyPlaneBombSprite(bombList, gameScore)

        //测试爆炸动画
        //TestComposeShowBombSprite()

        //得分
        ComposeScore(gameScore)

        //游戏开始界面
        GameOverBoard(gameState, gameScore, onGameAction)

    }

}

@InternalCoroutinesApi
@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@Preview()
@Composable
fun PreviewStage() {
    val gameViewModel: GameViewModel = viewModel()
    Stage(gameViewModel)
}
