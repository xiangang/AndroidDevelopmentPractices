package com.nxg.mvvm.logger


import mu.KLoggable
import mu.KLogger


interface SimpleLogger : KLoggable {

    override val logger: KLogger
        get() = logger(this::class.simpleName!!)
}