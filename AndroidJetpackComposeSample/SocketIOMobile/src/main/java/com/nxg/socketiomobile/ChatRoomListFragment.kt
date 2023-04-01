package com.nxg.socketiomobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.material.Text
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.nxg.socketiomobile.data.Message
import com.nxg.socketiomobile.data.SampleData
import com.nxg.socketiomobile.ui.theme.ComposeTutorialTheme

class ChatRoomListFragment : Fragment() {

    companion object {
        fun newInstance() = ChatRoomListFragment()
    }

    private lateinit var viewModel: ChatRoomListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                ComposeTutorialTheme {
                    Conversation(SampleData.conversationSample)
                }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[ChatRoomListViewModel::class.java]
        // TODO: Use the ViewModel
    }

    @Composable
    fun MessageCard(msg: Message) {

    }

    @Preview
    @Composable
    fun PreviewMessageCard() {
        MessageCard(
            msg = Message("Colleague", "Hey, take a look at Jetpack Compose, it's great!")
        )
    }


    @Composable
    fun Conversation(messages: List<Message>) {
        LazyColumn {
            items(messages) { message ->
                MessageCard(message)
            }
        }
    }

    @Preview
    @Composable
    fun PreviewConversation() {
        ComposeTutorialTheme {
            Conversation(SampleData.conversationSample)
        }
    }


}