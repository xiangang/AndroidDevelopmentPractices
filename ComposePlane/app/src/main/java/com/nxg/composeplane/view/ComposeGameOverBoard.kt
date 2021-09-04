package com.nxg.composeplane.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nxg.composeplane.model.GameState
import com.nxg.composeplane.model.OnGameAction
import com.nxg.composeplane.ui.theme.COLOR_999
import com.nxg.composeplane.util.LogUtil
import com.nxg.composeplane.util.ScoreFontFamily
import kotlinx.coroutines.InternalCoroutinesApi


/**
 * 游戏结束面板
 */
@InternalCoroutinesApi
@Composable
fun GameOverBoard(
    gameState: GameState,
    gameScore: Int,
    onGameAction: OnGameAction = OnGameAction()
) {
    LogUtil.printLog(message = "GameOverBoard()")
    Box(
        modifier = Modifier
            .wrapContentSize()
            .alpha(if (gameState == GameState.Over) 1.0f else 0f)
    ) {

        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val offsetLeft = size.width * 0.1f
            val offsetTop = size.height * 0.4f
            val rectWidth = (canvasWidth - offsetLeft * 2f)
            val rectHeight = (canvasHeight - offsetTop * 2)

            drawRect(
                color = COLOR_999,
                topLeft = Offset(x = offsetLeft, y = offsetTop),
                size = Size(
                    rectWidth,
                    rectHeight
                )
            )

            drawLine(
                start = Offset(offsetLeft, offsetTop),
                end = Offset(offsetLeft + rectWidth, offsetTop),
                color = Color.Black,
                strokeWidth = 5F
            )

            drawLine(
                start = Offset(offsetLeft, offsetTop),
                end = Offset(offsetLeft, offsetTop + rectHeight),
                color = Color.Black,
                strokeWidth = 5F
            )

            drawLine(
                start = Offset(offsetLeft, offsetTop + rectHeight),
                end = Offset(offsetLeft + rectWidth, offsetTop + rectHeight),
                color = Color.Black,
                strokeWidth = 5F
            )

            drawLine(
                start = Offset(offsetLeft + rectWidth, offsetTop + rectHeight),
                end = Offset(offsetLeft + rectWidth, offsetTop),
                color = Color.Black,
                strokeWidth = 5F
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
        ) {

            Spacer(
                modifier = Modifier
                    .weight(2f)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterHorizontally)
            ) {


                Spacer(
                    modifier = Modifier
                        .size(30.dp)
                )

                Text(
                    text = "score: $gameScore",
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp)
                        .align(Alignment.CenterHorizontally)
                        .wrapContentWidth(Alignment.End),
                    style = MaterialTheme.typography.h5,
                    color = Color.Black,
                    fontFamily = ScoreFontFamily
                )

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentSize()
                        .align(Alignment.CenterHorizontally)
                        .background(Color.Transparent),
                ) {

                    Spacer(
                        modifier = Modifier
                            .size(40.dp)
                    )

                    TextButton(
                        onClick = onGameAction.onReset,
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentSize()
                            .align(Alignment.CenterVertically)
                            .background(Color.Transparent),
                        border = BorderStroke(1.dp, Color.DarkGray),
                        content = {
                            Text(
                                text = "重新开始",
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .wrapContentWidth(Alignment.End),
                                style = MaterialTheme.typography.h5,
                                color = Color.Black,
                                fontFamily = ScoreFontFamily
                            )
                        }
                    )

                    TextButton(
                        onClick = onGameAction.onExit,
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentSize()
                            .align(Alignment.CenterVertically)
                            .background(Color.Transparent),
                        border = BorderStroke(1.dp, Color.DarkGray),
                        content = {
                            Text(
                                text = "退出游戏",
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .wrapContentWidth(Alignment.End),
                                style = MaterialTheme.typography.h5,
                                color = Color.Black,
                                fontFamily = ScoreFontFamily
                            )
                        }
                    )

                    Spacer(
                        modifier = Modifier
                            .size(40.dp)
                    )
                }

                Spacer(
                    modifier = Modifier
                        .size(30.dp)
                )
            }

            Spacer(
                modifier = Modifier
                    .weight(2f)
            )

        }

    }
}

@InternalCoroutinesApi
@Preview()
@Composable
fun PreviewGameOverBoard() {
    FarBackground()
    GameOverBoard(GameState.Over, 100, OnGameAction())
}

