package com.nxg.im.core.exception

sealed class IMException(message: String) : Exception() {

    class ApiException(message: String) : IMException(message)

    object TokenInvalidException : IMException("Token is not valid or has expired")

    object IllegalArgumentException : IMException("Illegal Argument")
}