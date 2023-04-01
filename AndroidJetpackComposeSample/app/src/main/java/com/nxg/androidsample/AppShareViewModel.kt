package com.nxg.androidsample

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * 作用域范围为Application的共享ShareViewModel
 * 使用方法：
 * ```
 * class MyAppCompatActivity : AppCompatActivity() {
 *     val appShareViewModel: AppShareViewModel by applicationViewModels()
 * }
 *
 * class MyFragment : Fragment() {
 *     val appShareViewModel: AppShareViewModel by applicationViewModels()
 * }
 * ```
 */
class AppShareViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * 单Activity架构
     */
    private val _uiState = MutableStateFlow(UiState.MAIN)

    // The UI collects from this StateFlow to get its state updates
    val uiState: StateFlow<UiState> = _uiState

    init {
        viewModelScope.launch {
            _uiState.value = UiState.MAIN
        }
    }

    fun setUiState(state: UiState) {
        viewModelScope.launch {
            _uiState.value = state
        }
    }

    /**
     * 界面状态
     */
    enum class UiState {
        MAIN, PERMISSION, NUI
    }

}