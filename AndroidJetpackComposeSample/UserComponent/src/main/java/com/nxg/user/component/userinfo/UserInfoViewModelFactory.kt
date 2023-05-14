package com.nxg.user.component.userinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nxg.im.core.module.auth.LoginDataSource
import com.nxg.user.component.login.LoginRepository

/**
 * ViewModel provider factory to instantiate UserInfoViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
class UserInfoViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserInfoViewModel::class.java)) {
            return UserInfoViewModel(
                loginRepository = LoginRepository(
                    dataSource = LoginDataSource.instance
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}