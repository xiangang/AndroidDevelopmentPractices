package com.nxg.im.conversation.component

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import coil.compose.AsyncImage
import com.nxg.commonui.theme.ColorBackground
import com.nxg.im.commonui.theme.JetchatTheme
import com.nxg.im.core.data.bean.*
import com.nxg.im.core.data.db.entity.Conversation
import com.nxg.mvvm.ktx.findMainActivityNavController
import com.nxg.mvvm.ui.BaseViewModelFragment
import kotlinx.coroutines.launch

class ConversationListFragment : BaseViewModelFragment() {

    private val conversationViewModel: ConversationViewModel by activityViewModels {
        ConversationViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        conversationViewModel.refresh()
        return ComposeView(requireContext()).apply {
            setContent {
                JetchatTheme {
                    val scope = rememberCoroutineScope()
                    ConversationListCompose(
                        conversationViewModel,
                        findMainActivityNavController()
                    ) { navController, conversation ->
                        scope.launch {
                            conversationViewModel.loadConversation(conversation)
                            val request = NavDeepLinkRequest.Builder
                                .fromUri("android-app://com.nxg.app/conversation_chat_fragment/${conversation.chatType}?chatId=${conversation.chatId}".toUri())
                                .build()
                            navController.navigate(request)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConversationListCompose(
    conversationViewModel: ConversationViewModel,
    navController: NavController,
    onClick: (NavController, Conversation) -> Unit
) {
    val uiState by conversationViewModel.uiState.collectAsState()
    LazyColumn(
        modifier = Modifier
            .background(ColorBackground.Primary)
            .fillMaxSize()
    ) {
        uiState.conversations.forEach { conversation ->
            item {
                ConversationItemCompose(
                    conversation,
                    navController,
                    onClick
                )
            }


        }

    }
}

@Composable
fun ConversationItemCompose(
    conversation: Conversation,
    navController: NavController,
    onClick: (NavController, Conversation) -> Unit
) {
    val cornerSize by remember { mutableStateOf(CornerSize(4.dp)) }
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            onClick(navController, conversation)
        }
        .padding(10.dp, 10.dp, 10.dp, 0.dp), verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            modifier = Modifier
                .size(60.dp)
                .clip(
                    MaterialTheme.shapes.small.copy(
                        topStart = cornerSize,
                        topEnd = cornerSize,
                        bottomEnd = cornerSize,
                        bottomStart = cornerSize
                    )
                ),
            model = conversation.coverImage,
            contentDescription = conversation.name
        )
        Spacer(modifier = Modifier.size(10.dp))
        Column {
            Text(conversation.name)
            conversation.lastIMMessage?.let {
                when (val content = it.content) {
                    is AudioMsgContent -> {}
                    is FileMsgContent -> {}
                    is ImageMsgContent -> {}
                    is LocationMsgContent -> {}
                    is TextMsgContent -> {
                        Text(content.text)
                    }
                    is VideoMsgContent -> {}
                }
            } ?: Text("")
        }
    }
}
