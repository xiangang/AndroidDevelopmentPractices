package com.nxg.im.user.component.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxg.im.core.module.user.User
import com.nxg.im.user.component.login.LoggedInUserView
import com.nxg.im.user.component.login.LoginRepository
import com.nxg.im.user.component.login.LoginResult
import com.nxg.mvvm.ktx.launchExceptionHandler
import com.nxg.mvvm.logger.SimpleLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(val user: User? = null)

class ProfileViewModel(private val loginRepository: LoginRepository) : ViewModel(), SimpleLogger {

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    // Backing property to avoid state updates from other classes
    private val _uiState = MutableStateFlow(ProfileUiState(null))

    // The UI collects from this StateFlow to get its state updates
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun getLoginData() {
        viewModelScope.launch {
            logger.debug { "getLoginData" }
            loginRepository.getLoginData()?.let {
                _loginResult.value =
                    LoginResult(
                        success = loginRepository.getLoginData()?.let { LoggedInUserView(it) })

                _uiState.emit(ProfileUiState(it.user))
                logger.debug { "user ${it.user}" }
            }
        }
    }

    suspend fun logout() {
        launchExceptionHandler(viewModelScope, Dispatchers.IO, {
            loginRepository.logout()
            _uiState.emit(ProfileUiState(null))
        }, onError = {
            logger.error { "login: error ${it.message}" }
        })
        viewModelScope.launch {
            _uiState.emit(ProfileUiState(null))
        }
    }
}