package com.nxg.mvvm.http

data class ApiResult<T>(val code: Int = 0, val message: String = "", val data: T? = null)