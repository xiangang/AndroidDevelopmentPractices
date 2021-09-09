package com.nxg.composeplane.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.nxg.composeplane.R
import com.nxg.composeplane.util.LogUtil

/**
 * 远处背景
 */
@Composable
fun FarBackground(
    modifier: Modifier = Modifier
) {
    LogUtil.printLog(message = "FarBackground()")

    Column {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentScale = ContentScale.FillBounds,
            contentDescription = null,
            modifier = modifier
                .fillMaxSize()
        )
    }
}

@Preview()
@Composable
fun PreviewBackground() {
    FarBackground(Modifier.fillMaxSize())
}
