package com.nxg.im.core

import com.nxg.im.core.module.auth.AuthService
import com.nxg.im.core.module.auth.AuthServiceImpl
import com.nxg.im.core.module.contact.RecentContact
import com.nxg.im.core.module.contact.RecentContactImpl
import com.nxg.im.core.module.user.UserService
import com.nxg.im.core.module.user.UserServiceImpl
import java.util.concurrent.ConcurrentHashMap

/**
 * IM客户端
 */
class IMClient private constructor() {

    val mapIMClientService = ConcurrentHashMap<String, IMService>()

    companion object {
        val instance by lazy {
            IMClient()
        }
    }

    init {
        mapIMClientService[AuthService::class.java.name] = AuthServiceImpl.instance
        mapIMClientService[UserService::class.java.name] = UserServiceImpl.instance
        mapIMClientService[RecentContact::class.java.name] = RecentContactImpl.instance
    }

    inline fun <reified T : IMService> getService(): T? {
        return mapIMClientService[T::class.java.name] as? T
    }

}