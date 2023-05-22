package com.nxg.im.conversation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxg.im.core.module.user.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ConversationUiState(
    val conversations: List<Conversation>,
    val conversation: Conversation? = null
)

class ConversationViewModel(val conversationRepository: ConversationRepository) : ViewModel() {


    // Backing property to avoid state updates from other classes
    private val _uiState = MutableStateFlow(ConversationUiState(emptyList()))

    // The UI collects from this StateFlow to get its state updates
    val uiState: StateFlow<ConversationUiState> = _uiState.asStateFlow()


    init {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val conversations = mutableListOf(
                    Conversation(
                        1, 0, User(
                            id = 1,
                            uuid = 51691563050860544,
                            username = "nxg",
                            password = "",
                            nickname = "失落轨迹",
                            email = "342005702@qq.com",
                            phone = "15607837955",
                            avatar = "https://randomuser.me/api/portraits/men/1.jpg",
                            address = "",
                            province = "",
                            city = "",
                            country = "",
                            status = 0,
                            createTime = "",
                            updateTime = ""
                        )
                    )
                )
                _uiState.emit(_uiState.value.copy(conversations = conversations))
            }
        }
    }

    suspend fun loadConversation(conversation: Conversation) {


    }

}

