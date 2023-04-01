package com.nxg.mvvm.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nxg.mvvm.R
import com.nxg.mvvm.navigation.NavigationCenter
import com.nxg.mvvm.navigation.NavigationDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class BaseSharedAndroidViewModel(application: Application) : AndroidViewModel(application) {

    private val _navigationDestination = MutableStateFlow(NavigationDestination.MAIN)

    // The UI collects from this StateFlow to get its state updates
    val navigationDestination: StateFlow<NavigationDestination> = _navigationDestination


    private val _navigationDestinationId = MutableStateFlow(-1)

    // The UI collects from this StateFlow to get its state updates
    val navigationDestinationId: StateFlow<Int> = _navigationDestinationId

    init {
        viewModelScope.launch {
            _navigationDestination.value = NavigationDestination.MAIN
        }
    }

    fun navigate(destination: NavigationDestination) {
        viewModelScope.launch {
            _navigationDestination.value = destination
        }
    }


    fun navigate(navigationResId: Int) {
        viewModelScope.launch {
            _navigationDestinationId.value = navigationResId
        }
    }

}