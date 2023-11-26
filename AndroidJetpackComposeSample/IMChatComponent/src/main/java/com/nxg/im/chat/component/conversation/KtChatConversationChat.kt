package com.nxg.im.chat.component.conversation

import android.annotation.SuppressLint
import android.content.ComponentCallbacks
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.paging.Pager
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import coil.compose.AsyncImage
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.MapView
import com.amap.api.maps.model.LatLng
import com.blankj.utilcode.util.TimeUtils
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.melody.map.gd_compose.overlay.rememberMarkerState
import com.melody.map.gd_compose.position.rememberCameraPositionState
import com.nxg.commonui.compose.LogCompositions
import com.nxg.im.chat.R
import com.nxg.im.chat.component.data.EMOJIS
import com.nxg.im.chat.component.data.EMOJIS.EMOJI_POINTS
import com.nxg.im.chat.component.notification.NotificationService.logger
import com.nxg.im.commonui.FunctionalityNotAvailablePopup
import com.nxg.im.commonui.components.SymbolAnnotationType
import com.nxg.im.commonui.components.UIMarkerInScreenCenter
import com.nxg.im.commonui.components.messageFormatter
import com.nxg.im.commonui.theme.BlueGrey30
import com.nxg.im.commonui.theme.Red40
import com.nxg.im.core.IMClient
import com.nxg.im.core.data.bean.*
import com.nxg.im.core.data.db.entity.*
import com.nxg.im.core.module.upload.UploadService
import com.nxg.im.core.module.user.User
import com.nxg.im.core.utils.VideoUtils
import kotlinx.coroutines.launch


@Composable
fun KtChatTextMessage(text: String, isUserMe: Boolean, authorClicked: (String) -> Unit = {}) {
    val uriHandler = LocalUriHandler.current
    val styledMessage = messageFormatter(
        text = text,
        primary = isUserMe
    )
    ClickableText(
        text = styledMessage,
        style = MaterialTheme.typography.bodyLarge.copy(color = LocalContentColor.current),
        modifier = Modifier.padding(16.dp),
        onClick = {
            styledMessage
                .getStringAnnotations(start = it, end = it)
                .firstOrNull()
                ?.let { annotation ->
                    when (annotation.tag) {
                        SymbolAnnotationType.LINK.name -> uriHandler.openUri(annotation.item)
                        SymbolAnnotationType.PERSON.name -> authorClicked(annotation.item)
                        else -> Unit
                    }
                }
        }
    )
}

@Preview
@Composable
fun KtChatTextMessagePreview() {
    KtChatTextMessage("Hello World!", true)
}

@Composable
fun KtChatImageMessage(url: String, isUserMe: Boolean, authorClicked: (String) -> Unit = {}) {
    AsyncImage(
        modifier = Modifier
            .fillMaxSize(),
        model = url,
        //placeholder = painterResource(R.drawable.ic_launcher_monochrome),
        contentScale = ContentScale.Crop,
        contentDescription = url,
    )

    /*SubcomposeAsyncImage(
        modifier = Modifier
            .fillMaxSize(),
        model = url,
        loading = {
            CircularProgressIndicator()
        },
        contentScale = ContentScale.Crop,
        contentDescription = url
    )*/

    /*SubcomposeAsyncImage(
        modifier = Modifier
            .fillMaxSize(),
        model = url,
        contentScale = ContentScale.Crop,
        contentDescription = url
    ) {
        val state = painter.state
        if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
            CircularProgressIndicator()
        } else {
            SubcomposeAsyncImageContent()
        }
    }*/

}

@Preview
@Composable
fun KtChatImageMessagePreview() {
    KtChatImageMessage(
        "https://img2.woyaogexing.com/2023/06/06/c1ad220f395db3f53f82ef5b502e7390.jpg",
        true
    )
}

@Composable
private fun KtChatAuthorNameTimestamp(name: String, timestamp: String, isUserMe: Boolean) {
    // Combine author and timestamp for a11y.
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {},
        horizontalArrangement = if (isUserMe) Arrangement.End else Arrangement.Start
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .alignBy(LastBaseline)
                .paddingFrom(LastBaseline, after = 8.dp) // Space to 1st bubble
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.alignBy(LastBaseline),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview
@Composable
fun KtChatAuthorNameTimestampPreview() {
    KtChatAuthorNameTimestamp("AnonXG", "2023年06月06日22:28:03", true)
}

@Composable
fun KtChatClickableMessage(
    message: Message,
    chatMessage: ChatMessage,
    isUserMe: Boolean,
    authorClicked: (String) -> Unit = {}
) {
    when (chatMessage) {
        is AudioMessage -> {}
        is FileMessage -> {}
        is ImageMessage -> {
            val uploadProgressState = remember { mutableIntStateOf(-1) }
            val uploadFilePath = message.uploadFilePath
            val showLoadingUI = chatMessage.content.url.isEmpty()
            Box(
                modifier = Modifier
                    .background(Color.Black)
                    .requiredWidth(100.dp)
                    .requiredHeightIn(min = if (chatMessage.content.width == 0) 100.dp else (chatMessage.content.height * 100 / chatMessage.content.width).dp)
            ) {
                LogCompositions(
                    tag = "ImageMessage",
                    msg = "chatMessage.content.url ${chatMessage.content.url}，${chatMessage.content.width}x${chatMessage.content.height}"
                )
                if (!showLoadingUI) {
                    KtChatImageMessage(
                        "${chatMessage.content.url}@400w_400h_60q",
                        true
                    )
                }
                if (showLoadingUI) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.Center)
                    ) {
                        // 创建一个 [InfiniteTransition] 实列用来管理子动画
                        val infiniteTransition = rememberInfiniteTransition(label = "")
                        // 创建一个float类型的子动画
                        val angle by infiniteTransition.animateFloat(
                            initialValue = 0F, //动画创建后，会从[initialValue] 执行至[targetValue]，
                            targetValue = 360F,
                            animationSpec = infiniteRepeatable(
                                //tween是补间动画，使用线性[LinearEasing]曲线无限重复1000 ms的补间动画
                                animation = tween(1500, easing = LinearEasing),
                            ), label = ""
                        )
                        LaunchedEffect(angle) {
                            if (IMClient.getService<UploadService>()
                                    .getUploadingFileProgress(uploadFilePath) > 0
                            ) {
                                val uploadProgress = IMClient.getService<UploadService>()
                                    .getUploadingFileProgress(uploadFilePath)
                                Log.i(
                                    "VideoMessage",
                                    "uploadProgress: $uploadFilePath $uploadProgress"
                                )
                                if (uploadProgressState.intValue != uploadProgress) {
                                    uploadProgressState.intValue = uploadProgress
                                }
                            }
                        }
                        Image(
                            modifier = Modifier
                                .alpha(1f)
                                .graphicsLayer { rotationZ = angle }
                                .size(60.dp)
                                .align(Alignment.CenterHorizontally),
                            painter = painterResource(id = R.drawable.ic_loading3),
                            contentDescription = ""
                        )
                        val uploadingText = if (uploadProgressState.intValue <= 0) {
                            ""
                        } else if (uploadProgressState.intValue == 100) {
                            "发送中"
                        } else {
                            "${uploadProgressState.intValue}%"
                        }
                        Text(
                            modifier = Modifier
                                .alpha(1f)
                                .padding(4.dp)
                                .align(Alignment.CenterHorizontally),
                            text = uploadingText,
                            fontSize = 14.sp
                        )
                    }

                }

            }
        }

        is VideoMessage -> {
            val uploadProgressState = remember { mutableIntStateOf(-1) }
            val uploadFilePath = message.uploadFilePath
            val showLoadingUI = chatMessage.content.url.isEmpty()
            Box(
                modifier = Modifier
                    .background(Color.Black)
                    .requiredWidth(100.dp)
                    .requiredHeightIn(min = if (chatMessage.content.width == 0) 100.dp else (chatMessage.content.height * 100 / chatMessage.content.width).dp)
            ) {
                LogCompositions(
                    tag = "VideoMessage",
                    msg = "$chatMessage"
                )

                if (!showLoadingUI) {
                    KtChatImageMessage(
                        uploadFilePath,
                        true
                    )
                }
                //视频封面缩略图
                if (chatMessage.content.thumbnailUrl.isNotEmpty()) {
                    AsyncImage(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxSize(),
                        model = chatMessage.content.thumbnailUrl,
                        contentDescription = null
                    )
                    Image(
                        modifier = Modifier
                            .size(32.dp)
                            .alpha(0.8f)
                            .align(Alignment.Center),
                        painter = painterResource(id = R.drawable.ic_play_video_white),
                        contentDescription = ""
                    )
                    Text(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(4.dp),
                        color = Color.White,
                        text = VideoUtils.formatDuration(chatMessage.content.duration / 1000),
                        fontSize = 14.sp
                    )
                }
                if (showLoadingUI) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .align(Alignment.Center)
                    ) {
                        // 创建一个 [InfiniteTransition] 实列用来管理子动画
                        val infiniteTransition = rememberInfiniteTransition(label = "")
                        // 创建一个float类型的子动画
                        val angle by infiniteTransition.animateFloat(
                            initialValue = 0F, //动画创建后，会从[initialValue] 执行至[targetValue]，
                            targetValue = 360F,
                            animationSpec = infiniteRepeatable(
                                //tween是补间动画，使用线性[LinearEasing]曲线无限重复1000 ms的补间动画
                                animation = tween(1500, easing = LinearEasing),
                            ), label = "Uploading"
                        )
                        LaunchedEffect(angle) {
                            if (IMClient.getService<UploadService>()
                                    .getUploadingFileProgress(uploadFilePath) > 0
                            ) {
                                val uploadProgress = IMClient.getService<UploadService>()
                                    .getUploadingFileProgress(uploadFilePath)
                                Log.i(
                                    "VideoMessage",
                                    "uploadProgress: $uploadFilePath $uploadProgress"
                                )
                                if (uploadProgressState.intValue != uploadProgress) {
                                    uploadProgressState.intValue = uploadProgress
                                }
                            }
                        }
                        Image(
                            modifier = Modifier
                                .alpha(1f)
                                .graphicsLayer { rotationZ = angle }
                                .size(60.dp)
                                .align(Alignment.CenterHorizontally),
                            painter = painterResource(id = R.drawable.ic_loading3),
                            contentDescription = ""
                        )
                        val uploadingText = if (uploadProgressState.intValue <= 0) {
                            ""
                        } else if (uploadProgressState.intValue == 100) {
                            "发送中"
                        } else {
                            "${uploadProgressState.intValue}%"
                        }
                        Text(
                            modifier = Modifier
                                .alpha(1f)
                                .padding(4.dp)
                                .align(Alignment.CenterHorizontally),
                            text = uploadingText,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        is LocationMessage -> {
            val dragDropAnimatable = remember { Animatable(Size.Zero, Size.VectorConverter) }
            val cameraPositionState = rememberCameraPositionState()
            val currentLocation =
                remember { LatLng(chatMessage.content.latitude, chatMessage.content.longitude) }
            val markerState = rememberMarkerState(position = currentLocation)
            LogCompositions("KtChatClickableMessage", "LocationMessage  cameraPositionState.move")
            LaunchedEffect(markerState.position) {
                logger.debug { "LocationMessage  cameraPositionState.move ${markerState.position}" }
                cameraPositionState.move(
                    CameraUpdateFactory.newLatLngZoom(
                        markerState.position,
                        17F
                    )
                )
            }
            val staticMapUrl = remember {
                val latitudeLongitude =
                    "${chatMessage.content.longitude},${chatMessage.content.latitude}"
                "https://restapi.amap.com/v3/staticmap?location=${latitudeLongitude}&zoom=17&size=750*300&key=4fe4aebd7a9a70facc42b5b9960d0c4c"
            }
            LogCompositions("KtChatClickableMessage", "staticMapUrl $staticMapUrl")
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    modifier = Modifier.padding(4.dp),
                    text = chatMessage.content.name,
                    fontSize = 12.sp
                )
                Text(
                    modifier = Modifier.padding(4.dp, 0.dp, 4.dp, 4.dp),
                    text = chatMessage.content.address,
                    fontSize = 10.sp
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(Color.Transparent)
                ) {
                    // Avatar
                    AsyncImage(
                        modifier = Modifier
                            .fillMaxSize(),
                        model = staticMapUrl,
                        contentScale = ContentScale.FillWidth,
                        contentDescription = null,
                    )

                    // 地图加载出来之后，再显示出来选点的图标
                    UIMarkerInScreenCenter(R.drawable.purple_pin) {
                        dragDropAnimatable.value
                    }
                }
            }
        }

        is TextMessage -> {
            KtChatTextMessage(chatMessage.content.text, isUserMe, authorClicked)
        }

        else -> {}
    }
}

@Preview
@Composable
fun KtChatClickableMessagePreview() {
    val textMsgContent = TextMsgContent(
        "Compose newbie as well ${EMOJIS.EMOJI_FLAMINGO}, have you looked at the JetNews sample? " +
                "Most blog posts end up out of date pretty fast but this sample is always up to " +
                "date and deals with async data loading (it's faked but the same idea " +
                "applies) $EMOJI_POINTS https://goo.gle/jetnews",
    )
    val textMessage = TextMessage(0, 0, 0, textMsgContent, System.currentTimeMillis())
    val message = Message(0, 0, 0, 1, 0, textMessage.toJson())
    KtChatClickableMessage(message, textMessage, true)//@Preview
}

private val KtChatBubbleShape = RoundedCornerShape(0.dp, 10.dp, 10.dp, 10.dp)

private val KtChatBubbleShapeUserMe = RoundedCornerShape(10.dp, 0.dp, 10.dp, 10.dp)

@Composable
fun KtChatItemBubble(
    message: Message,
    chatMessage: ChatMessage,
    sent: IMSendStatus,
    isUserMe: Boolean,
    authorClicked: (String) -> Unit = {},
    resend: () -> Unit = {},
) {
    val backgroundBubbleColor = if (isUserMe) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    Row {
        if (isUserMe) {
            // 状态
            when (sent) {//发送状态
                IM_SEND_REQUEST -> {
                    // 创建一个 [InfiniteTransition] 实列用来管理子动画
                    val infiniteTransition = rememberInfiniteTransition(label = "")
                    // 创建一个float类型的子动画
                    val angle by infiniteTransition.animateFloat(
                        initialValue = 0F, //动画创建后，会从[initialValue] 执行至[targetValue]，
                        targetValue = 360F,
                        animationSpec = infiniteRepeatable(
                            //tween是补间动画，使用线性[LinearEasing]曲线无限重复1000 ms的补间动画
                            animation = tween(1000, easing = LinearEasing),
                        ), label = ""
                    )
                    Icon(
                        modifier = Modifier
                            .graphicsLayer { rotationZ = angle }
                            .size(30.dp)
                            .align(Alignment.CenterVertically),
                        imageVector = Icons.Filled.Loop,
                        tint = BlueGrey30,
                        contentDescription = null,
                    )
                }

                IM_SEND_FAILED -> {
                    // 状态
                    Icon(
                        modifier = Modifier
                            .background(Color.Transparent)
                            .clickable(onClick = { resend() })
                            .size(30.dp)
                            .align(Alignment.CenterVertically),
                        imageVector = Icons.Filled.Error,
                        tint = Red40,
                        contentDescription = null,
                    )
                }
            }

        }
        Surface(
            color = backgroundBubbleColor,
            shape = if (isUserMe) KtChatBubbleShapeUserMe else KtChatBubbleShape
        ) {
            KtChatClickableMessage(
                message = message,
                chatMessage = chatMessage,
                isUserMe = isUserMe,
                authorClicked = authorClicked
            )
        }
        if (!isUserMe) {
            when (sent) {
                IM_SEND_REQUEST -> {
                    // 状态
                    Image(
                        modifier = Modifier
                            .size(30.dp)
                            .align(Alignment.CenterVertically),
                        painter = painterResource(id = R.drawable.ic_baseline_loop_24),
                        colorFilter = ColorFilter.tint(BlueGrey30),
                        contentDescription = null,
                    )
                }

                IM_SEND_FAILED -> {
                    // 状态
                    Image(
                        modifier = Modifier
                            .clickable(onClick = { resend() })
                            .size(30.dp)
                            .align(Alignment.CenterVertically),
                        painter = painterResource(id = R.drawable.ic_baseline_error_24),
                        colorFilter = ColorFilter.tint(Red40),
                        contentDescription = null,
                    )
                }
            }

        }
    }
}

@Preview
@Composable
fun KtChatItemBubblePreview() {
    val textMsgContent = TextMsgContent(
        "Compose newbie as well ${EMOJIS.EMOJI_FLAMINGO}, have you looked at the JetNews sample? " +
                "Most blog posts end up out of date pretty fast but this sample is always up to " +
                "date and deals with async data loading (it's faked but the same idea " +
                "applies) $EMOJI_POINTS https://goo.gle/jetnews",
    )
    val textMessage = TextMessage(0, 0, 0, textMsgContent, System.currentTimeMillis())
    val message = Message(0, 0, 0, 1, 0, "")
    KtChatItemBubble(message, textMessage, 1, true)
}

@Composable
fun KtChatAuthorAndTextMessage(
    message: Message,
    chatMessage: ChatMessage,
    sent: IMSendStatus,
    name: String,
    timestamp: String,
    isUserMe: Boolean,
    modifier: Modifier = Modifier,
    authorClicked: (String) -> Unit = {},
    resend: () -> Unit = {},
) {
    Column(
        modifier = modifier,
        horizontalAlignment = if (isUserMe) Alignment.End else Alignment.Start
    ) {
        KtChatAuthorNameTimestamp(name, timestamp, isUserMe)
        KtChatItemBubble(
            message,
            chatMessage,
            sent,
            isUserMe,
            authorClicked = authorClicked,
            resend = resend
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}


@Preview
@Composable
fun KtChatAuthorAndTextMessagePreview() {
    val textMsgContent = TextMsgContent(
        "Compose newbie as well ${EMOJIS.EMOJI_FLAMINGO}, have you looked at the JetNews sample? " +
                "Most blog posts end up out of date pretty fast but this sample is always up to " +
                "date and deals with async data loading (it's faked but the same idea " +
                "applies) $EMOJI_POINTS https://goo.gle/jetnews",
    )
    val textMessage = TextMessage(0, 0, 0, textMsgContent, System.currentTimeMillis())

    val imageMsgContent = ImageMsgContent(
        400, 400, "https://img2.woyaogexing.com/2023/06/06/c1ad220f395db3f53f82ef5b502e7390.jpg"
    )
    val imageMessage = ImageMessage(0, 0, 0, imageMsgContent, System.currentTimeMillis())
    val message = Message(0, 0, 0, 1, 0, "")
    Column {
        KtChatAuthorAndTextMessage(
            message,
            textMessage,
            0,
            "AnonXG",
            "2023年06月06日23:31:19",
            true
        )
        KtChatAuthorAndTextMessage(message, imageMessage, 0, "nxg", "2023年06月06日23:31:20", false)
    }
}

@Composable
fun KtChatMessage(
    message: Message,
    chatMessage: ChatMessage,
    sent: IMSendStatus,
    avatar: String,
    name: String,
    timestamp: String,
    isUserMe: Boolean,
    onAuthorClick: (String) -> Unit = {},
    resend: () -> Unit = {},
    onChatMessageItemClick: (ChatMessage) -> Unit = {},
) {
    val borderColor = if (isUserMe) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.tertiary
    }
    val spaceBetweenAuthors = Modifier.padding(top = 8.dp)
    if (isUserMe) {
        Row(modifier = spaceBetweenAuthors, horizontalArrangement = Arrangement.End) {
            //消息
            KtChatAuthorAndTextMessage(
                message = message,
                chatMessage = chatMessage,
                sent = sent,
                isUserMe = isUserMe,
                name = name,
                timestamp = timestamp,
                authorClicked = onAuthorClick,
                resend = resend,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
                    .clickable {
                        onChatMessageItemClick(chatMessage)
                    }
            )
            // Avatar
            AsyncImage(
                modifier = Modifier
                    .clickable(onClick = {
                        onAuthorClick("${chatMessage.fromId}")
                    })
                    .padding(horizontal = 16.dp)
                    .size(42.dp)
                    .border(1.5.dp, borderColor, CircleShape)
                    .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape)
                    .clip(CircleShape)
                    .align(Alignment.Top),
                model = avatar,
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )
        }
    } else {
        Row(modifier = spaceBetweenAuthors) {
            // Avatar
            AsyncImage(
                modifier = Modifier
                    .clickable(onClick = {
                        onAuthorClick("${chatMessage.fromId}")
                    })
                    .padding(horizontal = 16.dp)
                    .size(42.dp)
                    .border(1.5.dp, borderColor, CircleShape)
                    .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape)
                    .clip(CircleShape)
                    .align(Alignment.Top),
                model = avatar,
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )
            //消息
            KtChatAuthorAndTextMessage(
                message = message,
                chatMessage = chatMessage,
                sent = sent,
                isUserMe = isUserMe,
                name = name,
                timestamp = timestamp,
                authorClicked = onAuthorClick,
                resend = resend,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .weight(1f)
                    .clickable {
                        onChatMessageItemClick(chatMessage)
                    }
            )
        }
    }

}

@Preview
@Composable
fun KtChatMessagePreview() {
    val textMsgContent = TextMsgContent(
        "Compose newbie as well ${EMOJIS.EMOJI_FLAMINGO}, have you looked at the JetNews sample? " +
                "Most blog posts end up out of date pretty fast but this sample is always up to " +
                "date and deals with async data loading (it's faked but the same idea " +
                "applies) $EMOJI_POINTS https://goo.gle/jetnews",
    )
    val textMessage = TextMessage(0, 0, 0, textMsgContent, System.currentTimeMillis())
    val imageMsgContent = ImageMsgContent(
        400, 400, "https://img2.woyaogexing.com/2023/06/06/c1ad220f395db3f53f82ef5b502e7390.jpg"
    )
    val imageMessage = ImageMessage(0, 0, 0, imageMsgContent, System.currentTimeMillis())
    val message = Message(0, 0, 0, 1, 0, "")
    Box {
        LazyColumn(
            reverseLayout = true,//反转方向，这样才符合IM聊天列表的设计，最新的在底部，最老的在顶部
            modifier = Modifier
                .testTag(ConversationTestTag)
                .fillMaxSize()
        ) {
            item {
                KtChatMessage(
                    message,
                    textMessage, 0,
                    "https://randomuser.me/api/portraits/men/1.jpg",
                    "AnonXG",
                    "2023年06月06日23:31:19",
                    true
                )

            }
            item {
                KtChatMessage(
                    message,
                    imageMessage, 0,
                    "https://randomuser.me/api/portraits/men/2.jpg",
                    "nxg",
                    "2023年06月06日23:31:20",
                    false
                )
            }
            item {
                KtChatMessage(
                    message,
                    textMessage, 0,
                    "https://randomuser.me/api/portraits/men/1.jpg",
                    "AnonXG",
                    "2023年06月06日23:31:19",
                    false
                )
            }
            item {
                KtChatMessage(
                    message,
                    textMessage, 0,
                    "https://randomuser.me/api/portraits/men/2.jpg",
                    "nxg",
                    "2023年06月06日23:31:20",
                    true
                )
            }
            item {
                KtChatMessage(
                    message,
                    imageMessage, 0,
                    "https://randomuser.me/api/portraits/men/2.jpg",
                    "nxg",
                    "2023年06月06日23:31:20",
                    true
                )
            }
        }
    }
}

private val KtChatJumpToBottomThreshold = 56.dp


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun KtChatMessages(
    pager: Pager<Int, Message>? = null,
    me: User,
    friend: Friend,
    onAuthorClick: (String) -> Unit,
    resend: (Message) -> Unit = {},
    scrollState: LazyListState,
    modifier: Modifier = Modifier,
    onChatMessageItemClick: (ChatMessage) -> Unit = {},
) {
    pager?.let {
        val lazyPagingItems = it.flow.collectAsLazyPagingItems()
        var itemCount by remember { mutableIntStateOf(lazyPagingItems.itemCount) }
        val scope = rememberCoroutineScope()
        Box(modifier = modifier) {
            LazyColumn(
                reverseLayout = true,
                state = scrollState,
                modifier = Modifier
                    .testTag(ConversationTestTag)
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                itemsIndexed(
                    items = lazyPagingItems,
                    // The key is important so the Lazy list can remember your
                    // scroll position when more items are fetched!
                    key = { _, message -> message.id }
                ) { _, message ->
                    if (message != null) {
                        val chatMessage = message.toChatMessage()
                        KtChatMessage(
                            message = message,
                            chatMessage = chatMessage,
                            message.sent,
                            avatar = if (chatMessage.fromId == me.uuid) {
                                me.avatar
                            } else {
                                friend.avatar
                            },
                            name = if (chatMessage.fromId == me.uuid) {
                                me.nickname
                            } else {
                                friend.nickname
                            },
                            isUserMe = chatMessage.fromId == me.uuid,
                            timestamp = TimeUtils.millis2String(chatMessage.timestamp),
                            onAuthorClick = { name -> onAuthorClick(name) },
                            resend = { resend(message) },
                            onChatMessageItemClick = onChatMessageItemClick
                        )
                        DayHeader(TimeUtils.millis2String(chatMessage.timestamp))
                    }

                }
            }
            //Log.i("TAG", "KtChatMessages: itemCount $itemCount, ${lazyPagingItems.itemCount} ")
            if (itemCount == lazyPagingItems.itemCount) {
                scope.launch {
                    if (scrollState.firstVisibleItemIndex != 0) {
                        scrollState.animateScrollToItem(0)
                    }
                }
            }
            itemCount = lazyPagingItems.itemCount
        }
    }

}

@Preview
@Composable
fun KtChatMessagesPreview() {

}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun KtChatConversationContent(
    conversationChatViewModel: ConversationChatViewModel,
    modifier: Modifier = Modifier,
    onAuthorClick: (String) -> Unit,
    resend: (Message) -> Unit = {},
    onMessageSent: (String) -> Unit = {},
    onNavigateUp: () -> Unit = {},
    onSelectorChange: (InputSelector) -> Unit = {},
    onChatMessageItemClick: (ChatMessage) -> Unit = {},
) {
    val scrollState = rememberLazyListState()
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)
    val scope = rememberCoroutineScope()
    val conversationChatUiState = conversationChatViewModel.uiState.collectAsState()
    var functionalityNotAvailablePopupShown by remember { mutableStateOf(false) }
    if (functionalityNotAvailablePopupShown) {
        FunctionalityNotAvailablePopup { functionalityNotAvailablePopupShown = false }
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        conversationChatUiState.value.conversationChat?.friend?.nickname?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .clickable(onClick = {
                                onNavigateUp()
                            })
                            .padding(horizontal = 12.dp, vertical = 16.dp)
                            .height(24.dp),
                        contentDescription = stringResource(id = R.string.search)
                    )
                },
                actions = {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .clickable(onClick = { functionalityNotAvailablePopupShown = true })
                            .padding(horizontal = 12.dp, vertical = 16.dp)
                            .height(24.dp),
                        contentDescription = stringResource(id = R.string.info)
                    )
                }
            )
        },
        // Exclude ime and navigation bar padding so this can be added by the UserInput composable
        contentWindowInsets = ScaffoldDefaults
            .contentWindowInsets
            .exclude(WindowInsets.navigationBars)
            .exclude(WindowInsets.ime),
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            conversationChatUiState.value.conversationChat?.let {
                KtChatMessages(
                    pager = conversationChatViewModel.messagePager,
                    me = it.me,
                    friend = it.friend,
                    onAuthorClick = onAuthorClick,
                    resend = resend,
                    modifier = Modifier.weight(1f),
                    scrollState = scrollState,
                    onChatMessageItemClick = onChatMessageItemClick,
                )
            }
            UserInput(
                onMessageSent = { content ->
                    onMessageSent(content)
                },
                resetScroll = {
                    scope.launch {
                        scrollState.scrollToItem(0)
                    }
                },
                // let this element handle the padding so that the elevation is shown behind the
                // navigation bar
                modifier = Modifier
                    .navigationBarsPadding()
                    .imePadding(),
                onSelectorChange = onSelectorChange
            )
        }
    }
}

// 管理地图生命周期
private fun MapView.lifecycleObserver(): LifecycleEventObserver =
    LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_CREATE -> this.onCreate(Bundle())
            Lifecycle.Event.ON_RESUME -> this.onResume() // 重新绘制加载地图
            Lifecycle.Event.ON_PAUSE -> this.onPause()  // 暂停地图的绘制
            Lifecycle.Event.ON_DESTROY -> this.onDestroy() // 销毁地图
            else -> {}
        }
    }

private fun MapView.componentCallbacks(): ComponentCallbacks =
    object : ComponentCallbacks {
        // 设备配置发生改变，组件还在运行时
        override fun onConfigurationChanged(config: Configuration) {}

        // 系统运行的内存不足时，可以通过实现该方法去释放内存或不需要的资源
        override fun onLowMemory() {
            // 调用地图的onLowMemory
            this@componentCallbacks.onLowMemory()
        }
    }


@Composable
private fun PlayerViewLifecycle(mapView: MapView) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(context, lifecycle, mapView) {
        val mapLifecycleObserver = mapView.lifecycleObserver()
        val callbacks = mapView.componentCallbacks()
        // 添加生命周期观察者
        lifecycle.addObserver(mapLifecycleObserver)
        // 注册ComponentCallback
        context.registerComponentCallbacks(callbacks)

        onDispose {
            // 删除生命周期观察者
            lifecycle.removeObserver(mapLifecycleObserver)
            // 取消注册ComponentCallback
            context.unregisterComponentCallbacks(callbacks)
        }
    }
}

