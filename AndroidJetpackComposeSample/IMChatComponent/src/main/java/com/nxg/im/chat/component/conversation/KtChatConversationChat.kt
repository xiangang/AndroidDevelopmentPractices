package com.nxg.im.chat.component.conversation

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.Pager
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import coil.compose.AsyncImage
import com.blankj.utilcode.util.TimeUtils
import com.nxg.im.chat.R
import com.nxg.im.chat.component.data.EMOJIS
import com.nxg.im.chat.component.data.EMOJIS.EMOJI_POINTS
import com.nxg.im.commonui.FunctionalityNotAvailablePopup
import com.nxg.im.commonui.components.SymbolAnnotationType
import com.nxg.im.commonui.components.messageFormatter
import com.nxg.im.core.data.bean.*
import com.nxg.im.core.data.db.entity.Friend
import com.nxg.im.core.data.db.entity.Message
import com.nxg.im.core.module.user.User
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
            .size(100.dp),
        model = url,
        contentDescription = url
    )
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
    imMessage: IMMessage,
    isUserMe: Boolean,
    authorClicked: (String) -> Unit = {}
) {
    when (imMessage) {
        is AudioMessage -> {}
        is FileMessage -> {}
        is ImageMessage -> {
            KtChatImageMessage(
                imMessage.content.url,
                true
            )
        }
        is LocationMessage -> {}
        is TextMessage -> {
            KtChatTextMessage(imMessage.content.text, isUserMe, authorClicked)
        }
        is VideoMessage -> {}
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
    KtChatClickableMessage(textMessage, true)
}

private val KtChatBubbleShape = RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)

private val KtChatBubbleShapeUserMe = RoundedCornerShape(20.dp, 4.dp, 20.dp, 20.dp)

@Composable
fun KtChatItemBubble(
    imMessage: IMMessage,
    isUserMe: Boolean,
    authorClicked: (String) -> Unit = {}
) {
    val backgroundBubbleColor = if (isUserMe) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    Column {
        Surface(
            color = backgroundBubbleColor,
            shape = if (isUserMe) KtChatBubbleShapeUserMe else KtChatBubbleShape
        ) {
            KtChatClickableMessage(
                imMessage = imMessage,
                isUserMe = isUserMe,
                authorClicked = authorClicked
            )
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
    KtChatItemBubble(textMessage, true)
}

@Composable
fun KtChatAuthorAndTextMessage(
    imMessage: IMMessage,
    name: String,
    timestamp: String,
    isUserMe: Boolean,
    modifier: Modifier = Modifier,
    authorClicked: (String) -> Unit = {}
) {
    Column(
        modifier = modifier,
        horizontalAlignment = if (isUserMe) Alignment.End else Alignment.Start
    ) {
        KtChatAuthorNameTimestamp(name, timestamp, isUserMe)
        KtChatItemBubble(imMessage, isUserMe, authorClicked = authorClicked)
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
        "https://img2.woyaogexing.com/2023/06/06/c1ad220f395db3f53f82ef5b502e7390.jpg", 400, 400
    )
    val imageMessage = ImageMessage(0, 0, 0, imageMsgContent, System.currentTimeMillis())
    Column {
        KtChatAuthorAndTextMessage(textMessage, "AnonXG", "2023年06月06日23:31:19", true)
        KtChatAuthorAndTextMessage(imageMessage, "nxg", "2023年06月06日23:31:20", false)
    }
}

@Composable
fun KtChatIMMessage(
    imMessage: IMMessage,
    avatar: String,
    name: String,
    timestamp: String,
    isUserMe: Boolean,
    onAuthorClick: (String) -> Unit = {}
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
                imMessage = imMessage,
                isUserMe = isUserMe,
                name = name,
                timestamp = timestamp,
                authorClicked = onAuthorClick,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            )
            // Avatar
            AsyncImage(
                modifier = Modifier
                    .clickable(onClick = {
                        onAuthorClick("${imMessage.fromId}")
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
                        onAuthorClick("${imMessage.fromId}")
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
                imMessage = imMessage,
                isUserMe = isUserMe,
                name = name,
                timestamp = timestamp,
                authorClicked = onAuthorClick,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .weight(1f)
            )
        }
    }

}

@Preview
@Composable
fun KtChatIMMessagePreview() {
    val textMsgContent = TextMsgContent(
        "Compose newbie as well ${EMOJIS.EMOJI_FLAMINGO}, have you looked at the JetNews sample? " +
                "Most blog posts end up out of date pretty fast but this sample is always up to " +
                "date and deals with async data loading (it's faked but the same idea " +
                "applies) $EMOJI_POINTS https://goo.gle/jetnews",
    )
    val textMessage = TextMessage(0, 0, 0, textMsgContent, System.currentTimeMillis())
    val imageMsgContent = ImageMsgContent(
        "https://img2.woyaogexing.com/2023/06/06/c1ad220f395db3f53f82ef5b502e7390.jpg", 400, 400
    )
    val imageMessage = ImageMessage(0, 0, 0, imageMsgContent, System.currentTimeMillis())
    Box {
        LazyColumn(
            reverseLayout = true,//反转方向，这样才符合IM聊天列表的设计，最新的在底部，最老的在顶部
            modifier = Modifier
                .testTag(ConversationTestTag)
                .fillMaxSize()
        ) {
            item {
                KtChatIMMessage(
                    textMessage,
                    "https://randomuser.me/api/portraits/men/1.jpg",
                    "AnonXG",
                    "2023年06月06日23:31:19",
                    true
                )

            }
            item {
                KtChatIMMessage(
                    imageMessage,
                    "https://randomuser.me/api/portraits/men/2.jpg",
                    "nxg",
                    "2023年06月06日23:31:20",
                    false
                )
            }
            item {
                KtChatIMMessage(
                    textMessage,
                    "https://randomuser.me/api/portraits/men/1.jpg",
                    "AnonXG",
                    "2023年06月06日23:31:19",
                    false
                )
            }
            item {
                KtChatIMMessage(
                    textMessage,
                    "https://randomuser.me/api/portraits/men/2.jpg",
                    "nxg",
                    "2023年06月06日23:31:20",
                    true
                )
            }
            item {
                KtChatIMMessage(
                    imageMessage,
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
fun KtChatIMMessages(
    pager: Pager<Int, Message>,
    me: User,
    friend: Friend,
    onAuthorClick: (String) -> Unit,
    scrollState: LazyListState,
    modifier: Modifier = Modifier
) {
    val lazyPagingItems = pager.flow.collectAsLazyPagingItems()
    var itemCount by remember { mutableStateOf(lazyPagingItems.itemCount) }
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
            ) { index, message ->
                if (message != null) {
                    val imMessage = message.toIMMessage()
                    KtChatIMMessage(
                        imMessage = imMessage,
                        avatar = if (imMessage.fromId == me.uuid) {
                            me.avatar
                        } else {
                            friend.avatar
                        },
                        name = if (imMessage.fromId == me.uuid) {
                            me.nickname
                        } else {
                            friend.nickname
                        },
                        isUserMe = imMessage.fromId == me.uuid,
                        timestamp = TimeUtils.millis2String(imMessage.timestamp),
                        onAuthorClick = { name -> onAuthorClick(name) }
                    )
                    if (index % 10 == 0) {
                        DayHeader(TimeUtils.millis2String(imMessage.timestamp))
                    }
                }

            }
        }
        Log.i("TAG", "KtChatMessages: itemCount $itemCount, ${lazyPagingItems.itemCount} ")
        if (itemCount == lazyPagingItems.itemCount) {
            scope.launch {
                Log.i(
                    "TAG",
                    "KtChatMessages:firstVisibleItemIndex ${scrollState.firstVisibleItemIndex} "
                )
                if (scrollState.firstVisibleItemIndex != 0) {
                    scrollState.animateScrollToItem(0)
                }
            }
        }
        itemCount = lazyPagingItems.itemCount
        // Jump to bottom button shows up when user scrolls past a threshold.
        // Convert to pixels:
        val jumpThreshold = with(LocalDensity.current) {
            KtChatJumpToBottomThreshold.toPx()
        }

        // Show the button if the first visible item is not the first one or if the offset is
        // greater than the threshold.
        val jumpToBottomButtonEnabled by remember {
            derivedStateOf {
                scrollState.firstVisibleItemIndex != 0 ||
                        scrollState.firstVisibleItemScrollOffset > jumpThreshold
            }
        }

        /* JumpToBottom(
             // Only show if the scroller is not at the bottom
             enabled = jumpToBottomButtonEnabled,
             onClicked = {
                 scope.launch {
                     scrollState.animateScrollToItem(0)
                 }
             },
             modifier = Modifier.align(Alignment.BottomCenter)
         )*/
    }
}

@Preview
@Composable
fun KtChatIMMessagesPreview() {

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KtChatConversationContent(
    conversationChatViewModel: ConversationChatViewModel,
    modifier: Modifier = Modifier,
    onAuthorClick: (String) -> Unit,
    onMessageSent: (String) -> Unit,
    onNavigateUp: () -> Unit,
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
                KtChatIMMessages(
                    pager = conversationChatViewModel.messagePager,
                    me = it.me,
                    friend = it.friend,
                    onAuthorClick = onAuthorClick,
                    modifier = Modifier.weight(1f),
                    scrollState = scrollState
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
                    .imePadding()
            )
        }
    }
}

