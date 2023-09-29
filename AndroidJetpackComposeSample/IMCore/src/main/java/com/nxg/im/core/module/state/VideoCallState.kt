package com.nxg.im.core.module.state

import com.nxg.im.core.data.bean.Session

sealed class VideoCallState {

    object Idle : VideoCallState()
    class CallOut(val session: Session) : VideoCallState()
    class CallIn(val session: Session) : VideoCallState()
    class Connected constructor(val session: Session) : VideoCallState()

}

sealed class VideoCallEvent {

    class CallOut(val session: Session) : VideoCallEvent()
    class CallIn(val session: Session) : VideoCallEvent()
    class RingBack(val session: Session) : VideoCallEvent()
    class Cancel(val session: Session) : VideoCallEvent()
    class Answer(val session: Session) : VideoCallEvent()
    class Connect(val session: Session) : VideoCallEvent()
    class Timeout(val session: Session) : VideoCallEvent()
    class Hangup(val session: Session) : VideoCallEvent()
    class Disconnect(val session: Session) : VideoCallEvent()
}