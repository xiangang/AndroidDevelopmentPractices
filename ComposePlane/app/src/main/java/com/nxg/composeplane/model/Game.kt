package com.nxg.composeplane.model

import androidx.annotation.DrawableRes

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
data class OnGameAction(
    val onStart: () -> Unit = {},
    val onRestart: () -> Unit = {},
    val onPlayerMove: (x: Int, y: Int) -> Unit = { _: Int, _: Int -> },
    val onScore: (score: Int) -> Unit = { _: Int -> },
    val onShooting: (resId: Int) -> Unit = { _: Int -> },
    val onDestroyAllEnemy: () -> Unit = {},
    val onLevelUp: (score: Int) -> Unit = { _: Int -> },
    val onDying: () -> Unit = {},
    val onOver: () -> Unit = {},
    val onExit: () -> Unit = {},
)
