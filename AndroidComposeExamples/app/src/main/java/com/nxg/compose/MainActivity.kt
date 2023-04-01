package com.nxg.compose

import android.animation.ArgbEvaluator
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nxg.compose.ui.theme.AndroidComposeExamplesTheme
import kotlinx.coroutines.InternalCoroutinesApi

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidComposeExamplesTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    //Greeting("Android")

                    TestComposeGradientColorAnimate2()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {

    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AndroidComposeExamplesTheme {
        Greeting("Android")
    }
}

/**
 * 测试渐变动画
 */
@Composable
fun TestComposeGradientColorAnimate() {

    //渐变色计算类
    val argbEvaluator by remember {
        mutableStateOf(ArgbEvaluator())
    }

    //触发渐变动画用的状态
    var state by remember {
        mutableStateOf(0)
    }

    //fraction:0.0f-1.0f
    var fraction by remember {
        mutableStateOf(0f)
    }

    //0f-1f的持续1秒的动画
    val anim = remember {
        TargetBasedAnimation(
            animationSpec = tween(
                durationMillis = 1000,//秒
                easing = LinearEasing
            ),
            typeConverter = Float.VectorConverter,
            initialValue = 0f,
            targetValue = 1f
        )
    }
    var playTime by remember { mutableStateOf(0L) }
    LaunchedEffect(state) {
        val startTime = withFrameNanos { it }
        do {
            playTime = withFrameNanos { it } - startTime
            fraction = anim.getValueFromNanos(playTime)
        } while (!anim.isFinishedFromNanos(playTime))

    }

    print("fraction $fraction")

    //获取过渡颜色值（十六进制ARGB）
    val colorVal =
        argbEvaluator.evaluate(fraction, 0xFF018786.toInt(), 0xFF3700B3.toInt()) as Int
    //转成Compose Color对象
    val bgColor = Color(colorVal)

    //显示
    Box(
        Modifier
            .fillMaxSize()
            .background(color = bgColor)
    ) {
        Box(modifier = Modifier.fillMaxSize(1f), contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.Red)
                    .clickable {
                        print("触发渐变动画")
                        state++
                    }, contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "颜色代码：\n $colorVal",
                    style = TextStyle(color = Color.White, fontSize = 12.sp)
                )
            }
        }
    }

}


/**
 * 测试渐变动画
 */
@Composable
fun TestComposeGradientColorAnimate2() {

    //触发渐变动画用的状态
    var state by remember {
        mutableStateOf(0)
    }



    print("state $state")

    Box(
        modifier = Modifier
            .size(100.dp)
            .background(Color.DarkGray)
            .offset(x = 100.dp, y = (100).dp)
            .clickable {
                print("触发渐变动画-------->")
                state++
            }
    )

}

