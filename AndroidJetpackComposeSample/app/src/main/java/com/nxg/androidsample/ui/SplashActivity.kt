package com.nxg.androidsample.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TargetBasedAnimation
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.nxg.androidsample.MainActivity
import com.nxg.androidsample.R
import com.nxg.commonui.theme.AndroidJetpackComposeSampleTheme
import com.nxg.commonui.theme.ColorText
import com.nxg.commonui.utils.hideSystemBars
import com.nxg.commonui.utils.transparentStatusBar
import com.nxg.commonutils.onStart

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setTheme(R.style.Theme_AndroidJetpackComposeSample_NoActionBar_Splash)
        transparentStatusBar()
        hideSystemBars()
        setContent {
            AndroidJetpackComposeSampleTheme {
                Surface(color = MaterialTheme.colors.background) {
                    Splash {
                        onStart<MainActivity>()
                        finish()
                    }
                }
            }
        }
    }

}

@Composable
fun Splash(countDownSec: Int = 3, startMainActivity: () -> Unit) {
    var time by remember {
        mutableStateOf(countDownSec)
    }
    //倒计时动画，匀速，从countDownSec->0
    val anim = remember {
        TargetBasedAnimation(
            animationSpec = tween(durationMillis = countDownSec * 1000, easing = LinearEasing),
            typeConverter = Int.VectorConverter,
            initialValue = countDownSec,
            targetValue = 0
        )
    }
    var playTime by remember { mutableStateOf(0L) }

    LaunchedEffect(anim) {
        val startTime = withFrameNanos { it }
        do {
            playTime = withFrameNanos { it } - startTime
            time = anim.getValueFromNanos(playTime)
            if (time == 0) {
                startMainActivity()
            }
        } while (!anim.isFinishedFromNanos(playTime))
    }
    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
    ) {
        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(R.drawable.ic_compose_landing_roadmap)
                .build()
        )
        Image(
            painter = painterResource(R.drawable.splash_ad_01),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()

        )
        /*Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()

        )*/
        Row(
            modifier = Modifier
                .background(Color.Transparent)
                .fillMaxSize(),
            horizontalArrangement = Arrangement.End
        ) {

            Box(
                modifier = Modifier
                    .width(110.dp)
                    .wrapContentHeight()
                    .padding(20.dp, 60.dp, 20.dp, 20.dp)
                    .background(Color.Black.copy(0.6f), shape = RoundedCornerShape(10))
                    .clickable {
                        time = -1
                        startMainActivity()
                    }, contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier
                        .padding(4.dp, 10.dp, 4.dp, 10.dp),
                    text = if (time <= 0) {
                        " 跳过 "
                    } else {
                        "跳过 ${time}s"
                    },
                    textAlign = TextAlign.Center,
                    style = TextStyle(color = Color.White, fontSize = 14.sp)
                )
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxWidth()
                    .padding(0.dp, 20.dp, 0.dp, 0.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painterResource(R.drawable.ic_rocket),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.size(30.dp)
                )
                Text(
                    modifier = Modifier
                        .padding(0.dp, 0.dp),
                    text = buildAnnotatedString {
                        append("好用的")
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Light,
                                color = ColorText.Primary
                            )
                        ) {
                            append("Compose UI Kit")
                        }
                    },
                    style = TextStyle(color = ColorText.Primary, fontSize = 16.sp)
                )
            }
            Text(
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxWidth()
                    .padding(0.dp, 20.dp, 0.dp, 10.dp),
                textAlign = TextAlign.Center,
                text = buildAnnotatedString {
                    append("@copyright 2022-2030")
                },
                style = TextStyle(
                    color = ColorText.Primary,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Light,
                )
            )
        }
    }

}

@Preview
@Composable
fun SplashPreview() {
    Splash {}
}