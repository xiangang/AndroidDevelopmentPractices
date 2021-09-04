package com.nxg.composeplane.model

import kotlinx.coroutines.InternalCoroutinesApi

/**
 * 游戏状态
 */
enum class GameState {
    Waiting, // wait to start
    Running, // gaming
    Paused, // pause
    Dying, // hit enemy and dying
    Over // over
}

/**
 * 游戏动作
 */
@InternalCoroutinesApi
data class OnGameAction(
    val onStart: () -> Unit = {}, //游戏状态进入Running，游戏中
    val onPause: () -> Unit = {},//游戏状态进入Paused，暂停
    val onReset: () -> Unit = {},//游戏状态进入Waiting，显示GameWaiting
    val onDying: () -> Unit = {},//游戏状态进入Dying，触发爆炸动画
    val onOver: () -> Unit = {},//游戏状态进入Over，显示GameOverBoard
    val onExit: () -> Unit = {},//退出游戏
    val onPlayerMove: (x: Int, y: Int) -> Unit = { _: Int, _: Int -> },//玩家移动
    val onScore: (score: Int) -> Unit = { _: Int -> },//更新分数
    val onAward: (award: Award) -> Unit = { _: Award -> },//获得奖励
    val onShooting: (resId: Int) -> Unit = { _: Int -> },//射击
    val onDestroyAllEnemy: () -> Unit = {},//摧毁所有敌机
    val onLevelUp: (score: Int) -> Unit = { _: Int -> },//难度升级
)
