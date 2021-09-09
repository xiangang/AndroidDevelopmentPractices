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
    Over, // over
    Exit // finish activity
}

/**
 * 游戏动作
 */
@InternalCoroutinesApi
data class GameAction(
    val start: () -> Unit = {}, //游戏状态进入Running，游戏中
    val pause: () -> Unit = {},//游戏状态进入Paused，暂停
    val reset: () -> Unit = {},//游戏状态进入Waiting，显示GameWaiting
    val die: () -> Unit = {},//游戏状态进入Dying，触发爆炸动画
    val over: () -> Unit = {},//游戏状态进入Over，显示GameOverBoard
    val exit: () -> Unit = {},//退出游戏
    val playerMove: (x: Int, y: Int) -> Unit = { _: Int, _: Int -> },//玩家移动
    val score: (score: Int) -> Unit = { _: Int -> },//更新分数
    val award: (award: Award) -> Unit = { _: Award -> },//获得奖励
    val shooting: (resId: Int) -> Unit = { _: Int -> },//射击
    val destroyAllEnemy: () -> Unit = {},//摧毁所有敌机
    val levelUp: (score: Int) -> Unit = { _: Int -> },//难度升级
)
