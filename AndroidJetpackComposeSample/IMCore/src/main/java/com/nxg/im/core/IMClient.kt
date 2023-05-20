package com.nxg.im.core

import com.nxg.im.core.dispatcher.IMCoroutineScope
import com.nxg.im.core.module.auth.AuthService
import com.nxg.im.core.module.auth.AuthServiceImpl
import com.nxg.im.core.module.contact.RecentContact
import com.nxg.im.core.module.contact.RecentContactImpl
import com.nxg.im.core.module.user.UserService
import com.nxg.im.core.module.user.UserServiceImpl
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

/**
 * IM客户端
 */
object IMClient {

    val mapIMClientService = ConcurrentHashMap<String, IMService>()

    init {
        mapIMClientService[AuthService::class.java.name] = AuthServiceImpl
        mapIMClientService[UserService::class.java.name] = UserServiceImpl
        mapIMClientService[RecentContact::class.java.name] = RecentContactImpl
    }

    /**
     * 初始化
     */
    fun init() {
        IMCoroutineScope.launch {
            AuthServiceImpl.init()
        }
    }

    inline fun <reified T : IMService> getService(): T? {
        return mapIMClientService[T::class.java.name] as? T
    }

}