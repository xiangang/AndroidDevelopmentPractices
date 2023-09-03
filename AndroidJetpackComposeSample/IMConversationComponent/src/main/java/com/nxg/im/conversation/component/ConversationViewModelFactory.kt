package com.nxg.im.conversation.component

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ConversationViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConversationViewModel::class.java)) {
            return ConversationViewModel(
                conversationRepository = ConversationRepository()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}