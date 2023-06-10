package com.nxg.im.conversation.component

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxg.im.core.IMClient
import com.nxg.im.core.data.db.entity.Conversation
import com.nxg.mvvm.logger.SimpleLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ConversationUiState(
    val conversations: List<Conversation>,
    val conversation: Conversation? = null
)

class ConversationViewModel(val conversationRepository: ConversationRepository) : ViewModel(),
    SimpleLogger {


    // Backing property to avoid state updates from other classes
    private val _uiState = MutableStateFlow(ConversationUiState(emptyList()))

    // The UI collects from this StateFlow to get its state updates
    val uiState: StateFlow<ConversationUiState> = _uiState.asStateFlow()


    fun refresh() {

        viewModelScope.launch {
            val conversations = IMClient.conversationService.getConversationList()
            logger.debug { "conversations: $conversations" }
            conversations.forEach {
                it.updateLastIMMessage()
            }
            _uiState.emit(_uiState.value.copy(conversations = conversations))
        }
    }

    fun loadConversation(conversation: Conversation) {


    }

}

