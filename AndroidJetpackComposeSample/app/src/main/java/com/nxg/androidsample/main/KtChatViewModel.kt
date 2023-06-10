package com.nxg.androidsample.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxg.mvvm.logger.SimpleLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class KtChatUiState(val title: String)

class KtChatViewModel : ViewModel(), SimpleLogger {

    // Backing property to avoid state updates from other classes
    private val _uiState = MutableStateFlow(KtChatUiState("聊天"))

    // The UI collects from this StateFlow to get its state updates
    val uiState: StateFlow<KtChatUiState> = _uiState.asStateFlow()

    init {

        viewModelScope.launch {
            logger.debug { "init" }

        }
    }

    fun changeTitle(newTitle: String) {
        viewModelScope.launch {
            _uiState.emit(_uiState.value.copy(title = newTitle))
        }
    }

    val navigationBars = listOf(
        Screen.Chat,
        Screen.Contact,
        Screen.Discover,
        Screen.Profile,
    )


}