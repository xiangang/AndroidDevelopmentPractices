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
import com.nxg.composeplane.ui.theme.ComposePlaneTheme
import com.nxg.composeplane.util.LogUtil
import com.nxg.composeplane.util.StatusBarUtil
import com.nxg.composeplane.view.Stage
import com.nxg.composeplane.viewmodel.GameViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

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

        //状态栏沉浸式
        StatusBarUtil.transparentStatusBar(this)

        //生命周期观察
        lifecycle.addObserver(GameLifecycleObserver(gameViewModel))

        //观察游戏状态
        lifecycleScope.launch {
            gameViewModel.gameStateFlow.collect {
                LogUtil.printLog(message = "lifecycleScope gameState $it")
                //退出app
                if (GameState.Exit == it) {
                    finish()
                }
            }
        }

        //绘制界面
        setContent {
            ComposePlaneTheme {
                Surface(color = MaterialTheme.colors.background) {
                    Stage(gameViewModel)
                }
            }
        }
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