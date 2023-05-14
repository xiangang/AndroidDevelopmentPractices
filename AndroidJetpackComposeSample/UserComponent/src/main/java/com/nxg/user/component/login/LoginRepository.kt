package com.nxg.user.component.login

import com.nxg.im.core.module.auth.AuthService
import com.nxg.im.core.module.auth.LoginDataSource
import com.nxg.mvvm.logger.SimpleLogger


/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(val dataSource: LoginDataSource) : SimpleLogger, AuthService by dataSource {


}