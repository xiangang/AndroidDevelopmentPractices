package com.nxg.im.core.module.auth

data class LoginForm(val username: String, val password: String)

data class LoginOutForm(val token: String)