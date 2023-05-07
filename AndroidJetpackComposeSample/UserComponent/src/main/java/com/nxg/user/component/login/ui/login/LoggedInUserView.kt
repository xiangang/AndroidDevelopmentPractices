package com.nxg.user.component.login.ui.login

import com.nxg.im.http.bean.LoginData

/**
 * User details post authentication that is exposed to the UI
 */
data class LoggedInUserView(
    val loginData: LoginData
)