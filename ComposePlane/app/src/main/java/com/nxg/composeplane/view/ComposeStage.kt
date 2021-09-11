package com.nxg.composeplane.view

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nxg.composeplane.model.GameAction
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
fun Stage(gameViewModel: GameViewModel) {

    LogUtil.printLog(message = "Stage -------> ")

    //状态提升到这里,介绍见官方文档：https://developer.android.google.cn/jetpack/compose/state#state-hoisting
    //这里主要是方便统一管理，也避免直接使用ViewModel导致无法预览（预览时viewModel()会报错）

    //获取游戏状态
    val gameState by gameViewModel.gameStateFlow.collectAsState()

    //获取游戏分数
    val gameScore by gameViewModel.gameScoreStateFlow.collectAsState(0)

    //获取玩家飞机
    val playerPlane by gameViewModel.playerPlaneStateFlow.collectAsState()

    //获取所有子弹
    val bulletList by gameViewModel.bulletListStateFlow.collectAsState()

    //获取所有奖励
    val awardList by gameViewModel.awardListStateFlow.collectAsState()

    //获取所有敌军
    val enemyPlaneList by gameViewModel.enemyPlaneListStateFlow.collectAsState()

    //获取游戏动作函数
    val gameAction: GameAction = gameViewModel.onGameAction

    val modifier = Modifier.fillMaxSize()

    Box(modifier = modifier) {

        // 远景
        FarBackground(modifier)

        //游戏开始界面
        GameStart(gameState, playerPlane, gameAction)

        //玩家飞机
        PlayerPlaneSprite(
            gameState,
            playerPlane,
            gameAction
        )

        //玩家飞机出场飞入动画
        PlayerPlaneAnimIn(
            gameState,
            playerPlane,
            gameAction
        )

        //玩家飞机爆炸动画
        PlayerPlaneBombSprite(gameState, playerPlane, gameAction)

        //敌军飞机
        EnemyPlaneSprite(
            gameState,
            gameScore,
            playerPlane,
            bulletList,
            enemyPlaneList,
            gameAction
        )

        //子弹
        BulletSprite(gameState, bulletList, gameAction)

        //奖励
        AwardSprite(gameState, playerPlane, awardList, gameAction)

        //爆炸道具
        BombAward(playerPlane, gameAction)

        //游戏得分
        GameScore(gameState, gameScore, gameAction)

        //游戏开始界面
        GameOver(gameState, gameScore, gameAction)

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
