package com.nxg.im.core.module.contact

import com.nxg.mvvm.logger.SimpleLogger

class RecentContactImpl private constructor() : RecentContact,SimpleLogger {

    companion object {
        val instance by lazy {
            RecentContactImpl()
        }
    }
}