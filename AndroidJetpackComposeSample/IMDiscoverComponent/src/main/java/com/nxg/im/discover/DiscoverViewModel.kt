package com.nxg.im.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxg.mvvm.logger.SimpleLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class Discovery(val icon: Int, val name: String)

data class DiscoveryUiState(val discoveryList: List<Discovery>)

class DiscoverViewModel() : ViewModel(), SimpleLogger {

    // Backing property to avoid state updates from other classes
    private val _uiState = MutableStateFlow(DiscoveryUiState(emptyList()))

    // The UI collects from this StateFlow to get its state updates
    val uiState: StateFlow<DiscoveryUiState> = _uiState.asStateFlow()

    init {

        viewModelScope.launch {
            logger.debug { "init" }

        }
    }


}