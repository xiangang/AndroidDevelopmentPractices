package com.nxg.composeplane.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.nxg.composeplane.R
import com.nxg.composeplane.util.LogUtil
import com.nxg.composeplane.util.ScoreFontFamily
import kotlinx.coroutines.InternalCoroutinesApi

/**
 * 得分
 */
@InternalCoroutinesApi
@Composable
fun ComposeScore(gameScore: Int) {
    LogUtil.printLog(message = "ComposeScore()")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .absolutePadding(top = 20.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.pause),
            contentScale = ContentScale.FillBounds,
            contentDescription = null,
            modifier = Modifier
                .padding(start = 4.dp)
                .size(40.dp)
                .alpha(0f)
        )

        Text(
            text = "score: $gameScore",
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp)
                .align(Alignment.CenterVertically)
                .wrapContentWidth(Alignment.End),
            style = MaterialTheme.typography.h5,
            color = Color.Black,
            fontFamily = ScoreFontFamily
        )

    }
}

@InternalCoroutinesApi
@Preview()
@Composable
fun PreviewComposeScore() {
    ComposeScore(800)
}
