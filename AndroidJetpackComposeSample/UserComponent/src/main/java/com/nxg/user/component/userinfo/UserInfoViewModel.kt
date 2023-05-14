package com.nxg.user.component.userinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxg.user.component.login.LoggedInUserView
import com.nxg.user.component.login.LoginRepository
import com.nxg.user.component.login.LoginResult
import kotlinx.coroutines.launch


class UserInfoViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    init {
        viewModelScope.launch {
            loginRepository.getLoginData()?.let {
                _loginResult.value =
                    LoginResult(
                        success = loginRepository.getLoginData()?.let { LoggedInUserView(it) })
            }

        }

    }
}