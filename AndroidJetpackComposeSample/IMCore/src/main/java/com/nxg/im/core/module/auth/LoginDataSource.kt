package com.nxg.im.core.module.auth

import com.nxg.mvvm.logger.SimpleLogger

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource private constructor() : AuthService by AuthServiceImpl,
    SimpleLogger {

    companion object {

        val instance by lazy {
            LoginDataSource()
        }
    }
}