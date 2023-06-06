package com.nxg.im.chat.component.conversation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nxg.im.chat.R
import com.nxg.im.chat.component.data.EMOJIS
import com.nxg.im.chat.component.data.EMOJIS.EMOJI_POINTS
import com.nxg.im.core.data.bean.*


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
    val backgroundBubbleColor = if (isUserMe) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    /*Image(
        painter = painterResource(R.drawable.sticker),
        contentScale = ContentScale.Fit,
        modifier = Modifier.size(160.dp),
        contentDescription = stringResource(id = R.string.attached_image)
    )*/
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
private fun KtChatAuthorNameTimestamp(name: String, timestamp: String) {
    // Combine author and timestamp for a11y.
    Row(modifier = Modifier.semantics(mergeDescendants = true) {}) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .alignBy(LastBaseline)
                .paddingFrom(LastBaseline, after = 8.dp) // Space to 1st bubble
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = timestamp,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.alignBy(LastBaseline),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview
@Composable
fun KtChatAuthorNameTimestampPreview() {
    KtChatAuthorNameTimestamp("AnonXG", "2023年06月06日22:28:03")
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
            shape = KtChatBubbleShape
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
    Column(modifier = modifier) {
        KtChatAuthorNameTimestamp(name, timestamp)
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