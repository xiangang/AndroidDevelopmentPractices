package com.nxg.im.core

import android.content.Context
import com.nxg.im.core.callback.OnMessageCallback
import com.nxg.im.core.data.db.KtChatDatabase
import com.nxg.im.core.dispatcher.IMCoroutineScope
import com.nxg.im.core.module.auth.AuthService
import com.nxg.im.core.module.auth.AuthServiceImpl
import com.nxg.im.core.module.chat.ChatService
import com.nxg.im.core.module.chat.ChatServiceImpl
import com.nxg.im.core.module.conversation.ConversationService
import com.nxg.im.core.module.conversation.ConversationServiceImpl
import com.nxg.im.core.module.user.UserService
import com.nxg.im.core.module.user.UserServiceImpl
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

/**
 * IM客户端
 */
object IMClient {

    val mapIMClientService = ConcurrentHashMap<String, IMService>()

    inline fun <reified T : IMService> getService(): T? {
        return mapIMClientService[T::class.java.name] as? T
    }


    val authService: AuthService by lazy {
        AuthServiceImpl
    }

    val userService: UserService by lazy {
        UserServiceImpl
    }

    val conversationService: ConversationService by lazy {
        ConversationServiceImpl
    }

    val chatService: ChatService by lazy {
        ChatServiceImpl
    }

    var onMessageCallback: OnMessageCallback? = null

    /**
     * 初始化
     */
    fun init(context: Context) {
        KtChatDatabase.getInstance(context.applicationContext)
        IMCoroutineScope.launch {
            authService.init()
        }
    }


}