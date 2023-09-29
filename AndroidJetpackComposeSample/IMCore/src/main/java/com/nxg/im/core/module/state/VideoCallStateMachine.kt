package com.nxg.im.core.module.state

import android.media.Ringtone
import com.nxg.im.core.IMClient
import com.nxg.im.core.R
import com.nxg.im.core.data.bean.Session
import com.nxg.im.core.dispatcher.IMCoroutineScope
import com.nxg.im.core.dispatcher.IMDispatcher
import com.nxg.im.core.module.mediaplayer.MediaPlayerService
import com.nxg.im.core.module.soundpool.SoundPoolRawRes
import com.nxg.im.core.module.soundpool.SoundPoolService
import com.nxg.mvvm.logger.SimpleLogger
import com.nxg.statemachine.StateMachine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


interface IVideoCallStateMachine {
    fun callOut(session: Session)
    fun callIn(session: Session)
    fun ringBack(session: Session) //CallOut和CallIn通了才会回铃
    fun cancel(session: Session)
    fun answer(session: Session)
    fun connect(session: Session)
    fun hangup(session: Session)
    fun disconnect(session: Session)
}

object VideoCallStateMachine : IVideoCallStateMachine, SimpleLogger {

    private val stateMachine: StateMachine<VideoCallState, VideoCallEvent> = StateMachine.create(
        "VideoCallStateMachine",
        IMDispatcher, VideoCallState.Idle, ::onEvent, ::onTransition
    )

    private val ringtone: Ringtone? = null

    val state: VideoCallState
        get() = stateMachine.state

    fun isIdle() = state == VideoCallState.Idle

    private val stateMutableStateFlow = MutableStateFlow(stateMachine.state)

    val stateFlow: StateFlow<VideoCallState>
        get() = stateMutableStateFlow

    override fun callOut(session: Session) {
        stateMachine.postEvent(VideoCallEvent.CallOut(session))
    }

    override fun callIn(session: Session) {
        stateMachine.postEvent(VideoCallEvent.CallIn(session))
    }

    override fun ringBack(session: Session) {
        stateMachine.postEvent(VideoCallEvent.RingBack(session))
    }

    override fun cancel(session: Session) {
        stateMachine.postEvent(VideoCallEvent.Cancel(session))
    }

    override fun answer(session: Session) {
        stateMachine.postEvent(VideoCallEvent.Answer(session))
    }

    override fun connect(session: Session) {
        stateMachine.postEvent(VideoCallEvent.Connect(session))
    }

    override fun hangup(session: Session) {
        stateMachine.postEvent(VideoCallEvent.Hangup(session))
    }

    override fun disconnect(session: Session) {
        stateMachine.postEvent(VideoCallEvent.Disconnect(session))
    }

    private fun onEvent(event: VideoCallEvent) {
        logger.debug { "onEvent: currentState $state, event $event" }
        when (event) {
            is VideoCallEvent.CallIn -> {
                if (state is VideoCallState.Idle) {
                    stateMachine.transition(VideoCallState.CallIn(event.session))
                }
            }

            is VideoCallEvent.RingBack -> {
                //振铃，无状态迁移
                IMClient.getService<MediaPlayerService>().play(R.raw.im_core_ring,true)
            }

            is VideoCallEvent.CallOut -> {
                if (state is VideoCallState.Idle) {
                    stateMachine.transition(VideoCallState.CallOut(event.session))
                }
            }

            is VideoCallEvent.Cancel -> {
                if (state is VideoCallState.CallOut) {
                    stateMachine.transition(VideoCallState.Idle)
                }
            }

            is VideoCallEvent.Answer -> {
                if (state is VideoCallState.CallOut) {
                    stateMachine.transition(VideoCallState.Connected(event.session))
                }
            }

            is VideoCallEvent.Connect -> {
                if (state is VideoCallState.CallOut || state is VideoCallState.CallIn) {
                    stateMachine.transition(VideoCallState.Connected(event.session))
                }
            }

            is VideoCallEvent.Hangup -> {
                if (state is VideoCallState.CallOut || state is VideoCallState.CallIn || state is VideoCallState.Connected) {
                    stateMachine.transition(VideoCallState.Idle)
                }

            }

            is VideoCallEvent.Timeout -> {
                if (state is VideoCallState.CallOut || state is VideoCallState.CallIn || state is VideoCallState.Connected) {
                    stateMachine.transition(VideoCallState.Idle)
                }
            }


            is VideoCallEvent.Disconnect -> {
                if (state is VideoCallState.Connected) {
                    stateMachine.transition(VideoCallState.Idle)
                }
            }
        }
    }

    private fun onTransition(preState: VideoCallState, state: VideoCallState) {
        logger.debug { "onTransition: preState:$preState, newState: $state" }
        stateMachine.coroutineScope.launch {
            when (state) {
                is VideoCallState.Idle -> {

                    when (preState) {
                        is VideoCallState.CallOut -> {
                            IMClient.videoCallService.removeSession(preState.session)
                            IMCoroutineScope.launch {
                                IMClient.getService<MediaPlayerService>().stop()
                                IMClient.getService<MediaPlayerService>()
                                    .play(R.raw.im_core_hangup, false)
                            }
                        }

                        is VideoCallState.CallIn -> {
                            IMClient.videoCallService.removeSession(preState.session)
                            IMCoroutineScope.launch {
                                IMClient.getService<MediaPlayerService>().stop()
                                delay(100)
                                IMClient.getService<MediaPlayerService>()
                                    .play(R.raw.im_core_hangup, false)
                            }
                        }

                        is VideoCallState.Connected -> {
                            IMClient.videoCallService.removeSession(preState.session)
                            IMCoroutineScope.launch {
                                IMClient.getService<MediaPlayerService>().stop()
                                delay(100)
                                IMClient.getService<MediaPlayerService>()
                                    .play(R.raw.im_core_hangup, false)
                            }
                        }

                        else -> {

                        }
                    }
                }

                is VideoCallState.CallOut -> {
                    IMClient.videoCallService.putSession(state.session)
                }

                is VideoCallState.CallIn -> {
                    IMClient.videoCallService.putSession(state.session)
                }

                is VideoCallState.Connected -> {
                    IMClient.getService<MediaPlayerService>().stop()
                    IMClient.videoCallService.putSession(state.session)
                }
            }
            logger.debug { "onTransition: stateMutableStateFlow.emit($state)" }
            stateMutableStateFlow.emit(state)
        }
    }
}