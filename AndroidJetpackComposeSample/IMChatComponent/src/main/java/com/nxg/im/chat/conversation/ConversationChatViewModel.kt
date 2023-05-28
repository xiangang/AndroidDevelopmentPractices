package com.nxg.im.chat.conversation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxg.im.core.module.user.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


data class ConversationChat(val id: Long, val type: Int, val messages: List<Message>)

data class ConversationChatUiState(
    val conversationChat: ConversationChat? = null
)

class ConversationChatViewModel(val conversationChatRepository: ConversationChatRepository) :
    ViewModel() {


    // Backing property to avoid state updates from other classes
    private val _uiState = MutableStateFlow(ConversationChatUiState(null))

    // The UI collects from this StateFlow to get its state updates
    val uiState: StateFlow<ConversationChatUiState> = _uiState.asStateFlow()


    init {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {


            }
        }
    }

    suspend fun loadConversationChat(conversationChat: ConversationChat) {
        _uiState.emit(_uiState.value.copy(conversationChat = conversationChat))

    }

}

