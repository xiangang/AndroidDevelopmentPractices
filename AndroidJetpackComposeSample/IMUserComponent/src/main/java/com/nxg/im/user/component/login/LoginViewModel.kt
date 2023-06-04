package com.nxg.im.user.component.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxg.im.core.data.bean.Result
import com.nxg.mvvm.ktx.launchExceptionHandler
import com.nxg.mvvm.logger.SimpleLogger
import com.nxg.user.component.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel(), SimpleLogger {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult?>()
    val loginResult: LiveData<LoginResult?> = _loginResult

    fun login(username: String, password: String) {
        launchExceptionHandler(viewModelScope, Dispatchers.IO, {
            val result = loginRepository.login(username, password)
            logger.debug { "login: result $result" }
            withContext(Dispatchers.Main) {
                when (result) {
                    is Result.Error -> {
                        _loginResult.value =
                            LoginResult(error = "Login Failed: ${result.exception.message}")
                    }

                    is Result.Success -> {
                        _loginResult.value =
                            LoginResult(success = LoggedInUserView(result.data))
                    }
                }
            }

        }, onError = {
            _loginResult.value = LoginResult(error = "Login Failed: ${it.message}")
            logger.error { "login: error ${it.message}" }
        })

    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains("@")) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    suspend fun logout() {
        viewModelScope.launch(Dispatchers.Main) {
            _loginResult.value = null
        }

    }
}