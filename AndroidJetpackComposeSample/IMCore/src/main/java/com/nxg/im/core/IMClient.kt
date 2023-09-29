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
import com.nxg.im.core.module.mediaplayer.MediaPlayerService
import com.nxg.im.core.module.mediaplayer.MediaPlayerServiceImpl
import com.nxg.im.core.module.ring.RingService
import com.nxg.im.core.module.ring.RingServiceImpl
import com.nxg.im.core.module.signaling.SignalingService
import com.nxg.im.core.module.signaling.SignalingServiceImpl
import com.nxg.im.core.module.soundpool.SoundPoolService
import com.nxg.im.core.module.soundpool.SoundPoolServiceImpl
import com.nxg.im.core.module.user.UserService
import com.nxg.im.core.module.user.UserServiceImpl
import com.nxg.im.core.module.videocall.VideoCallService
import com.nxg.im.core.module.videocall.VideoCallServiceImpl
import com.nxg.mvvm.logger.SimpleLogger
import kotlinx.coroutines.launch

/**
 * IM客户端
 */
object IMClient : SimpleLogger {

    inline fun <reified T : IMService> getService(): T {
        logger.debug { "IMClient getService:  ${T::class.java.name} ${T::class.sealedSubclasses} ${T::class.objectInstance!!}" }
        return when (T::class) {
            AuthService::class -> AuthServiceImpl as T
            UserService::class -> UserServiceImpl as T
            ConversationService::class -> ConversationServiceImpl as T
            ChatService::class -> ChatServiceImpl as T
            SignalingService::class -> AuthServiceImpl as T
            VideoCallService::class -> VideoCallServiceImpl as T
            RingService::class -> RingServiceImpl as T
            MediaPlayerService::class -> MediaPlayerServiceImpl as T
            SoundPoolService::class -> SoundPoolServiceImpl as T
            else -> {
                throw IllegalArgumentException("Can't find instance of the ${T::class.java} implementation class!")
            }
        }

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

    val signalingService: SignalingService by lazy {
        SignalingServiceImpl
    }

    val videoCallService: VideoCallService by lazy {
        VideoCallServiceImpl
    }

    var onMessageCallback: OnMessageCallback? = null

    /**
     * 初始化
     */
    fun init(context: Context) {
        KtChatDatabase.getInstance(context.applicationContext)
        IMCoroutineScope.launch {
            authService.init()
            SoundPoolServiceImpl.init()
        }
    }


}