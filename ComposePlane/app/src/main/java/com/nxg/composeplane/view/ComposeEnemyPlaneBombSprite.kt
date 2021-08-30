package com.nxg.composeplane.view

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nxg.composeplane.model.Bomb
import com.nxg.composeplane.model.EnemyPlane
import com.nxg.composeplane.model.GameState
import com.nxg.composeplane.util.LogUtil
import com.nxg.composeplane.viewmodel.GameViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay

/**
 * 敌军飞机爆炸动画
 */
@InternalCoroutinesApi
@ExperimentalAnimationApi
@Composable
fun ComposeEnemyPlaneBombSprite(
    bombList: List<Bomb>,
    gameScore: Int
) {
    for (bomb in bombList) {
        ComposeShowBombSprite(gameScore, bomb)
        LogUtil.printLog(message = "ComposeEnemyPlaneBombSprite() bomb ${bomb}")
    }

}

@InternalCoroutinesApi
@ExperimentalAnimationApi
@Composable
fun ComposeShowBombSprite(
    gameScore: Int = 0,
    bomb: Bomb
) {
    //准备爆炸动画
    LogUtil.printLog(message = "ComposeShowBombSprite() bomb ${bomb.id} ------------------------------ segment = ${bomb.segment}")
    val segment = bomb.segment
    val anim = remember {
        TargetBasedAnimation(
            animationSpec = tween(bomb.segment * 30),
            typeConverter = Int.VectorConverter,
            initialValue = 0,
            targetValue = segment - 1
        )
    }
    var animationSegmentIndex by remember {
        mutableStateOf(0)
    }

    var playTime by remember { mutableStateOf(0L) }

    LogUtil.printLog(message = "ComposeShowBombSprite() bomb.state ${bomb.state} ")

    LaunchedEffect(gameScore) {
        val startTime = withFrameNanos { it }
        do {
            playTime = withFrameNanos { it } - startTime
            animationSegmentIndex = anim.getValueFromNanos(playTime)
        } while (!anim.isFinishedFromNanos(playTime))

    }
    LogUtil.printLog(message = "ComposeShowBombSprite() bomb ${bomb.id} --------------> animationSegmentIndex $animationSegmentIndex")

    //越界检测
    if (animationSegmentIndex >= bomb.segment) {
        return
    }

    //初始化炸弹的大小
    val bombWidth = bomb.width
    val bombWidthWidthPx = with(LocalDensity.current) { bombWidth.toPx() }

    //这里使用修改ImageBitmap.imageResource返回bitmap方便处理
    val bitmap: Bitmap = imageResource(bomb.bombDrawableId)

    //分割Bitmap
    val displayBitmapWidth = bitmap.width / bomb.segment

    //Matrix用来放大到跟EnemyPlane一样大小
    val matrix = Matrix()
    matrix.postScale(
        bombWidthWidthPx / displayBitmapWidth,
        bombWidthWidthPx / bitmap.height
    )
    LogUtil.printLog(message = "ComposeShowBombSprite() bomb ${bomb.id} bitmap.width ${bitmap.width}")
    LogUtil.printLog(message = "ComposeShowBombSprite() bomb ${bomb.id} displayBitmapWidth $displayBitmapWidth")
    LogUtil.printLog(message = "ComposeShowBombSprite() bomb ${bomb.id} animationSegmentIndex * displayBitmapWidth ${animationSegmentIndex * displayBitmapWidth}")

    //越界检测
    if ((animationSegmentIndex * displayBitmapWidth) + displayBitmapWidth > bitmap.width) {
        return
    }

    //只获取需要的部分
    val displayBitmap = Bitmap.createBitmap(
        bitmap,
        (animationSegmentIndex * displayBitmapWidth),
        0,
        displayBitmapWidth,
        bitmap.height,
        matrix,
        true
    )
    val imageBitmap: ImageBitmap = displayBitmap.asImageBitmap()
    val imageBitmapWidth = imageBitmap.width
    val imageBitmapHeight = imageBitmap.height
    LogUtil.printLog(message = "ComposeShowBombSprite() bomb ${bomb.id} bombWidthWidthPx $bombWidthWidthPx")
    LogUtil.printLog(message = "ComposeShowBombSprite() bomb ${bomb.id} animationValue * displayBitmapWidth) ${animationSegmentIndex * displayBitmapWidth}")
    LogUtil.printLog(message = "ComposeShowBombSprite() bomb ${bomb.id} imageBitmapWidth $imageBitmapWidth")
    LogUtil.printLog(message = "ComposeShowBombSprite() bomb ${bomb.id} imageBitmapHeight $imageBitmapHeight")
    LogUtil.printLog(message = "ComposeShowBombSprite() bomb ${bomb.id} animationSegmentIndex $animationSegmentIndex")
    LogUtil.printLog(message = "ComposeShowBombSprite() bomb ${bomb.id} ------------------------------bomb $bomb")

    //播放到最后一帧动画就进入死亡状态
    if (animationSegmentIndex == bomb.segment - 1) {
        bomb.die()
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .size(bombWidth)
    ) {

        drawImage(
            imageBitmap,
            topLeft = Offset(
                bomb.x.toFloat(),
                bomb.y.toFloat(),
            ),
            alpha = if (bomb.isAlive()) 1.0f else 0f,
        )
    }

}

/**
 * 这里使用状态提升，状态下沉，控制上升
 */
@InternalCoroutinesApi
@ExperimentalAnimationApi
@Composable
fun ComposeShowEnemyPlaneBomb(
    gameScore: Int = 0,
    enemyPlane: EnemyPlane,
    showBombAnim: Boolean,
    onBombAnimChange: (Boolean) -> Unit
) {
    //准备爆炸动画
    LogUtil.printLog(message = "ComposeShowEnemyPlaneBomb() showBombAnim $showBombAnim, enemyPlane ${enemyPlane.id} ------------------------------ segment = ${enemyPlane.segment}")

    val segment = enemyPlane.segment
    val anim = remember {
        TargetBasedAnimation(
            animationSpec = tween(durationMillis = enemyPlane.segment * (1000 / 60)),//相当一秒播放30帧
            typeConverter = Int.VectorConverter,
            initialValue = 0,
            targetValue = segment - 1
        )
    }

    var animationSegmentIndex by remember { mutableStateOf(0) }
    var playTime by remember { mutableStateOf(0L) }
    LaunchedEffect(gameScore) {
        val startTime = withFrameNanos { it }
        do {
            playTime = withFrameNanos { it } - startTime
            animationSegmentIndex = anim.getValueFromNanos(playTime)
        } while (!anim.isFinishedFromNanos(playTime))

    }
    LogUtil.printLog(message = "ComposeShowEnemyPlaneBomb() ---> animationSegmentIndex = $animationSegmentIndex, enemyPlane =  $enemyPlane")

    //越界检测
    if (animationSegmentIndex >= enemyPlane.segment) {
        return
    }

    //初始化炸弹的大小
    val bombWidth = enemyPlane.width
    val bombWidthWidthPx = with(LocalDensity.current) { bombWidth.toPx() }

    //这里使用修改ImageBitmap.imageResource返回bitmap方便处理
    val bitmap: Bitmap = imageResource(enemyPlane.bombDrawableId)

    //分割Bitmap
    val displayBitmapWidth = bitmap.width / enemyPlane.segment

    //Matrix用来放大到跟EnemyPlane一样大小
    val matrix = Matrix()
    matrix.postScale(
        bombWidthWidthPx / displayBitmapWidth,
        bombWidthWidthPx / bitmap.height
    )

    //越界检测
    if ((animationSegmentIndex * displayBitmapWidth) + displayBitmapWidth > bitmap.width) {
        return
    }

    //只获取需要的部分
    val displayBitmap = Bitmap.createBitmap(
        bitmap,
        (animationSegmentIndex * displayBitmapWidth),
        0,
        displayBitmapWidth,
        bitmap.height,
        matrix,
        true
    )
    val imageBitmap: ImageBitmap = displayBitmap.asImageBitmap()

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .size(bombWidth)
    ) {
        drawImage(
            imageBitmap,
            topLeft = Offset(
                enemyPlane.x.toFloat(),
                enemyPlane.y.toFloat(),
            ),
            alpha = if (showBombAnim) 1.0f else 0f,
        )
    }


    //播放到最后一帧动画就进入死亡状态
    LogUtil.printLog(message = "ComposeShowEnemyPlaneBomb() ---> before showBombAnim = $showBombAnim, animationSegmentIndex = $animationSegmentIndex ")
    if (animationSegmentIndex == enemyPlane.segment - 2 && showBombAnim) {
        onBombAnimChange(false)
    }
    LogUtil.printLog(message = "ComposeShowEnemyPlaneBomb() ---> after showBombAnim = $showBombAnim, animationSegmentIndex = $animationSegmentIndex ")

}

@Composable
fun TextAndTextField(name: String, onValueChange: (String) -> Unit) {

    Column(modifier = Modifier.padding(16.dp)) {

        Text(
            text = "Hello，$name",
            modifier = Modifier.padding(bottom = 6.dp),
            style = MaterialTheme.typography.h5,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        OutlinedTextField(
            value = name,
            onValueChange = onValueChange,
            label = { Text(text = "name") })
    }

}

/**
 * 测试爆炸动画
 */
@InternalCoroutinesApi
@Composable
fun TestComposeShowBombSprite() {

    val bomb by remember { mutableStateOf(Bomb(x = 500, y = 500)) }

    var state by remember {
        mutableStateOf(0)
    }

    val anim = remember {
        TargetBasedAnimation(
            animationSpec = tween(
                durationMillis = bomb.segment * 33,//相当一秒播放30帧， 1000/30 = 33
                easing = LinearEasing
            ),
            typeConverter = Int.VectorConverter,
            initialValue = 0,
            targetValue = bomb.segment - 1
        )
    }

    var playTime by remember { mutableStateOf(0L) }
    var animationSegmentIndex by remember {
        mutableStateOf(0)
    }

    LaunchedEffect(state) {
        val startTime = withFrameNanos { it }
        do {
            playTime = withFrameNanos { it } - startTime
            animationSegmentIndex = anim.getValueFromNanos(playTime)
        } while (!anim.isFinishedFromNanos(playTime))

    }

    Box(modifier = Modifier.fillMaxSize(1f), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(Color.Red, shape = RoundedCornerShape(60 / 5))
                .clickable {
                    LogUtil.printLog(message = "触发动画 ")
                    state++
                    bomb.reBirth()
                }, contentAlignment = Alignment.Center
        ) {
            Text(
                text = animationSegmentIndex.toString(),
                style = TextStyle(color = Color.White, fontSize = 12.sp)
            )
        }
    }
    LogUtil.printLog(message = "TestComposeShowBombSprite() animationSegmentIndex $animationSegmentIndex")
    LogUtil.printLog(message = "TestComposeShowBombSprite() bomb.state ${bomb.state}")

    PlayBombSpriteAnimate(bomb, animationSegmentIndex)

}

@InternalCoroutinesApi
@Composable
fun PlayBombSpriteAnimate(bomb: Bomb, animationSegmentIndex: Int) {
    LogUtil.printLog(message = "PlayBombSpriteAnimate() -----------bomb $bomb")
    //越界检测
    if (animationSegmentIndex >= bomb.segment) {
        return
    }
    //初始化炸弹的大小
    val bombWidth = bomb.width
    val bombWidthWidthPx = with(LocalDensity.current) { bombWidth.toPx() }

    //这里使用修改ImageBitmap.imageResource返回bitmap方便处理
    val bitmap: Bitmap = imageResource(bomb.bombDrawableId)

    //分割Bitmap
    val displayBitmapWidth = bitmap.width / bomb.segment

    //Matrix用来放大到跟bombWidthWidthPx一样大小
    val matrix = Matrix()
    matrix.postScale(
        bombWidthWidthPx / displayBitmapWidth,
        bombWidthWidthPx / bitmap.height
    )

    //越界检测
    if ((animationSegmentIndex * displayBitmapWidth) + displayBitmapWidth > bitmap.width) {
        return
    }

    //只获取需要的部分
    val displayBitmap = Bitmap.createBitmap(
        bitmap,
        (animationSegmentIndex * displayBitmapWidth),
        0,
        displayBitmapWidth,
        bitmap.height,
        matrix,
        true
    )
    val imageBitmap: ImageBitmap = displayBitmap.asImageBitmap()

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .size(bombWidth)
    ) {

        drawImage(
            imageBitmap,
            topLeft = Offset(
                bomb.x.toFloat(),
                bomb.y.toFloat(),
            ),
            alpha = if (bomb.isAlive()) 1.0f else 0f,
        )
    }
}


@InternalCoroutinesApi
@ExperimentalAnimationApi
@Preview()
@Composable
fun PreviewEnemyPlaneBombSprite() {
}
