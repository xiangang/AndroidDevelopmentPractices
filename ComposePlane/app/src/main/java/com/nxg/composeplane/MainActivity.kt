package com.nxg.composeplane

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nxg.composeplane.model.GameState
import com.nxg.composeplane.model.OnGameAction
import com.nxg.composeplane.ui.theme.ComposePlaneTheme
import com.nxg.composeplane.util.LogUtil
import com.nxg.composeplane.util.SoundPoolUtil
import com.nxg.composeplane.util.StatusBarUtil
import com.nxg.composeplane.view.Stage
import com.nxg.composeplane.viewmodel.GameViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

class MainActivity : ComponentActivity() {

    @InternalCoroutinesApi
    private val gameViewModel: GameViewModel by viewModels()

    @InternalCoroutinesApi
    @ExperimentalComposeUiApi
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //启动页
        installSplashScreen()

        // 状态栏沉浸式
        StatusBarUtil.transparentStatusBar(this)

        //生命周期观察
        lifecycle.addObserver(GameLifecycleObserver(gameViewModel))

        //观察游戏状态
        lifecycleScope.launch {
            gameViewModel.gameStateFlow.collect {
                LogUtil.printLog(message = "lifecycleScope gameState $it")
            }
        }

        //游戏动作
        val onGameAction = OnGameAction(

            onStart = {
                gameViewModel.onGameStateFlowChange(GameState.Running)
            },

            onRestart = {
                gameViewModel.onGameStateFlowChange(GameState.Waiting)
            },
            onPlayerMove = { x, y ->
                run {
                    gameViewModel.onPlayerPlaneMove(x, y)
                }
            },
            onScore = { score ->
                run {
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            SoundPoolUtil.getInstance(applicationContext)
                                .playByRes(R.raw.explosion)//播放res中的音频
                        }
                    }
                    gameViewModel.onGameScoreChange(score)

                    //简单处理，不同分数对应不同的等级
                    if (score in 100..999) {
                        gameViewModel.onGameLevelChange(2)
                    }
                    if (score in 1000..1999) {
                        gameViewModel.onGameLevelChange(3)
                    }
                    //模拟奖励双发子弹有100发
                    if (score == 100) {
                        gameViewModel.onPlayerAwardBullet(1 shl 16 or 100)
                    }
                }
            },
            onDying = {
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        SoundPoolUtil.getInstance(applicationContext)
                            .playByRes(R.raw.explosion)//播放res中的音频
                    }
                }

                gameViewModel.onGameStateFlowChange(GameState.Dying)

            },
            onOver = {
                gameViewModel.onGameStateFlowChange(GameState.Over)
            },
            onExit = {
                finish()
            },
            onDestroyAllEnemy = {
                LogUtil.printLog(message = "onDestroyAllEnemy --->")
                gameViewModel.onDestroyAllEnemy()
            },
            onShooting = { resId ->
                run {
                    LogUtil.printLog(message = "onShooting resId $resId")
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            SoundPoolUtil.getInstance(applicationContext)
                                .playByRes(resId)//播放res中的音频
                        }
                    }
                }
            },
        )

        //绘制
        setContent {
            ComposePlaneTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {

                    //利用协程定时生成子弹，目前没有用到敌机上。
                    LaunchedEffect(key1 = Unit) {
                        while (isActive) {
                            delay(100)
                            gameViewModel.createBulletSprite()
                        }
                    }

                    Stage(gameViewModel, onGameAction)
                }
            }
        }
    }

    @InternalCoroutinesApi
    override fun onPause() {
        super.onPause()
        //gameViewModel.onGameStateFlowChange(GameState.Paused)
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