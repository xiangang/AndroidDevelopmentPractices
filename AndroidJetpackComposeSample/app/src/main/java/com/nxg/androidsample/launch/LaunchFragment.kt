package com.nxg.androidsample.launch

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TargetBasedAnimation
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.findNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.nxg.commonui.theme.ColorText
import com.nxg.im.commonui.theme.JetchatTheme
import com.nxg.im.core.IMClient
import com.nxg.im.core.http.IMHttpManger
import com.nxg.im.core.module.auth.AuthService
import com.nxg.mvvm.logger.SimpleLogger
import com.nxg.mvvm.ui.BaseViewModelFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * 启动页
 */
class LaunchFragment : BaseViewModelFragment(), SimpleLogger {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                JetchatTheme {
                    // Remember a SystemUiController
                    val systemUiController = rememberSystemUiController()
                    val useDarkIcons = !isSystemInDarkTheme()
                    requireActivity().apply {
                        // 设置全屏
                        /*window.setFlags(
                            WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN
                        )*/
                        //隐藏状态栏、导航栏、标题栏
                        WindowCompat.getInsetsController(window, window.decorView)
                            .hide(WindowInsetsCompat.Type.systemBars())
                    }
                    DisposableEffect(systemUiController, useDarkIcons) {
                        // Update all of the system bar colors to be transparent, and use
                        // dark icons if we're in light theme
                        systemUiController.setSystemBarsColor(
                            color = if (!useDarkIcons) Color.Black else Color.White,
                            darkIcons = useDarkIcons
                        )

                        // setStatusBarColor() and setNavigationBarColor() also exist

                        onDispose {}
                    }
                    Surface(color = if (!useDarkIcons) Color.Black else Color.White) {
                        val scope = rememberCoroutineScope()
                        Splash {
                            scope.launch(Dispatchers.IO) {
                                IMClient.getService<AuthService>()?.getApiToken()?.let {
                                    try {
                                        IMHttpManger.imApiService.me(it)
                                        withContext(Dispatchers.Main) {
                                            findNavController().navigate(LaunchFragmentDirections.actionLaunchFragmentToMainFragment())
                                        }
                                    } catch (e: Exception) {
                                        logger.debug { "me: ${e.message}" }
                                        withContext(Dispatchers.Main) {
                                            findNavController().navigate(LaunchFragmentDirections.actionLaunchFragmentToLoginFragment())
                                        }
                                    }

                                } ?: withContext(Dispatchers.Main) {
                                    findNavController().navigate(LaunchFragmentDirections.actionLaunchFragmentToLoginFragment())
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().apply {
            //取消全屏
            //window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            //显示状态栏、导航栏、标题栏
            WindowCompat.getInsetsController(requireActivity().window, window.decorView)
                .show(WindowInsetsCompat.Type.systemBars())
        }

    }

    @Composable
    fun Splash(countDownSec: Int = 3, navigationTo: () -> Unit) {
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
                    navigationTo()
                }
            } while (!anim.isFinishedFromNanos(playTime))
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(com.nxg.androidsample.R.drawable.ic_compose_landing_roadmap)
                    .build()
            )
            AnimatedVisibility(visible = (time < 3), enter = fadeIn()) {
                Image(
                    painter = painterResource(com.nxg.androidsample.R.drawable.splash_default),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()

                )
                /*Image(
                    painter = painter,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )*/
            }
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
                        .padding(20.dp, 20.dp, 20.dp, 20.dp)
                        .background(Color.Black.copy(0.6f), shape = RoundedCornerShape(10))
                        .clickable {
                            time = -1
                            navigationTo()
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

            AnimatedVisibility(visible = (time < 2), enter = fadeIn()) {
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
                            painterResource(com.nxg.androidsample.R.drawable.ic_rocket),
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

    }

    @Preview
    @Composable
    fun SplashPreview() {
        Splash {}
    }

}