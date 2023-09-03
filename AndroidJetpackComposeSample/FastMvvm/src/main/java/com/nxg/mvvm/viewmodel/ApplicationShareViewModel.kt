package com.nxg.mvvm.viewmodel

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

object NoneNavDirections : NavDirections {
    override val actionId: Int
        get() = -1
    override val arguments: Bundle
        get() = Bundle()
}

class ApplicationShareViewModel(application: Application) : AndroidViewModel(application) {

    private val _navDirectionsStateFlow = MutableSharedFlow<String>(0)

    val navDirectionsStateFlow: SharedFlow<String> = _navDirectionsStateFlow.asSharedFlow()

    fun navigate(directions: String) {
        viewModelScope.launch {
            _navDirectionsStateFlow.emit(directions)
        }
    }
}