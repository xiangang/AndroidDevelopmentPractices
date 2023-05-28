package com.nxg.im.discover

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxg.mvvm.logger.SimpleLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class Discovery(val icon: ImageVector, val name: String)

data class DiscoveryUiState(val discoveryList: List<Discovery>)

class DiscoverViewModel() : ViewModel(), SimpleLogger {

    // Backing property to avoid state updates from other classes
    private val _uiState = MutableStateFlow(DiscoveryUiState(emptyList()))

    // The UI collects from this StateFlow to get its state updates
    val uiState: StateFlow<DiscoveryUiState> = _uiState.asStateFlow()

    init {

        viewModelScope.launch {
            _uiState.emit(
                _uiState.value.copy(
                    discoveryList = listOf(
                        Discovery(Icons.Rounded.Camera, "朋友圈"),
                        Discovery(Icons.Rounded.ShoppingBag, "购物"),
                        Discovery(Icons.Rounded.Mail, "邮箱"),
                    )
                )
            )

        }
    }


}