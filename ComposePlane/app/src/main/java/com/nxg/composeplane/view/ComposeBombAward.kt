package com.nxg.composeplane.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.nxg.composeplane.R
import com.nxg.composeplane.model.OnGameAction
import com.nxg.composeplane.model.PlayerPlane
import com.nxg.composeplane.util.LogUtil
import com.nxg.composeplane.util.ScoreFontFamily
import kotlinx.coroutines.InternalCoroutinesApi
import kotlin.math.roundToInt

/**
 * 爆炸奖励
 */
@InternalCoroutinesApi
@Composable
fun ComposeBombAward(
    playerPlane: PlayerPlane = PlayerPlane(),
    gameAction: OnGameAction = OnGameAction()
) {
    LogUtil.printLog(message = "ComposeBombAward()")
    //初始化必要的参数
    val heightPixels = LocalContext.current.resources.displayMetrics.heightPixels
    val bombAward = playerPlane.bombAward
    val bombNum = bombAward and 0xFFFF //数量
    val bombType = bombAward shr 16 //类型
    val bombWidth = 44.dp
    val bombHeight = 40.dp
    val bombWidthPx = with(LocalDensity.current) { bombWidth.toPx() }
    val bombHeightPx = with(LocalDensity.current) { bombHeight.toPx() }
    val offsetY = heightPixels - bombHeightPx / 2
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Column(
            Modifier
                .fillMaxSize()
                .padding(top = 20.dp)
                .alpha(if (bombNum > 0) 1f else 0f)
        ) {
            Spacer(
                modifier = Modifier
                    .weight(1f)
            )
            Row(
                modifier = Modifier
                    .padding(10.dp)
                    .wrapContentSize()
                //.offset { IntOffset(bombWidthPx.roundToInt(), offsetY.roundToInt()) }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.sprite_red_bomb),
                    contentScale = ContentScale.FillBounds,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .size(bombWidth, bombHeight)
                        .clickable(onClick = gameAction.onDestroyAllEnemy)
                )

                Text(
                    text = " x $bombNum",
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .align(Alignment.CenterVertically)
                        .wrapContentWidth(Alignment.End),
                    style = MaterialTheme.typography.h4,
                    color = Color.Black,
                    fontFamily = ScoreFontFamily
                )

            }
        }

    }

}

@InternalCoroutinesApi
@Preview()
@Composable
fun PreviewComposeBombAward() {
    ComposeBombAward()
}
