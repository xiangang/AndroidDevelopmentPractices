package com.nxg.user.component.login

import com.nxg.im.core.module.auth.LoginData

/**
 * User details post authentication that is exposed to the UI
 */
data class LoggedInUserView(
    val loginData: LoginData
)