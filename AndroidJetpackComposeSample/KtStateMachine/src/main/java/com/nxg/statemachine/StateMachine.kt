package com.nxg.statemachine

import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicReference

/*
 * 有限状态机，（英语：Finite-state machine, FSM），又称有限状态自动机，简称状态机，是表示有限个状态以及在这些状态之间的转移和动作等行为的数学模型。、
 * 以下是对状态机抽象定义
 * State（状态）：构成状态机的基本单位。 状态机在任何特定时间都可处于某一状态。从生命周期来看有Initial State、End State、Suspend State(挂起状态)
 * Event（事件）：导致转换发生的事件活动
 * Transitions（转换器）：两个状态之间的定向转换关系，状态机对发生的特定类型事件响应后当前状态由A转换到B。标准转换、选择转换、子流程转换多种抽象实现
 * Actions（转换操作）：在执行某个转换时执行的具体操作。
 * Guards（检测器）：检测器出现的原因是为了转换操作执行后检测结果是否满足特定条件从一个状态切换到某一个状态
 * Interceptor（拦截器）：对当前状态改变前、后进行监听拦截。
 * 此状态机是分支法实现
 */
class StateMachine<STATE : Any, EVENT : Any> private constructor(
    private val name: String,//状态机名称
    coroutineDispatcher: CoroutineDispatcher,
    initialState: STATE,//初始状态,
    private val onEvent: (EVENT) -> Unit,//事件处理,
    private val onTransition: (STATE, STATE) -> Unit//监听状态转移
) {

    /**
     * 协程作用域
     */
    val coroutineScope: CoroutineScope =
        CoroutineScope(coroutineDispatcher + SupervisorJob())

    /**
     * 使用Atomic支持多线程
     */
    private val stateRef = AtomicReference(initialState)

    /**
     * 当前状态，定义为val不给外部直接修改
     */
    val state: STATE
        get() = stateRef.get()

    /**
     * 阻塞型事件发送
     * @param event EVENT
     */
    fun sendEvent(event: EVENT) {
        onEvent(event)
    }

    /**
     * 非阻塞型事件发送
     * @param event EVENT
     */
    fun postEvent(event: EVENT) {
        coroutineScope.launch {
            sendEvent(event)
        }
    }

    /**
     * 状态转换
     * @param state STATE
     */
    fun transition(state: STATE) {
        coroutineScope.launch {
            val preState = stateRef.get()
            println("transition: $preState to $state")
            stateRef.set(state)
            onTransition(preState, state)
        }
    }

    override fun toString(): String {
        return "StateMachine(name='$name')"
    }

    companion object {

        /**
         * 创建一个状态机
         * @param name String 状态机名称
         * @param initialState STATE 初始状态
         * @param onEvent Function1<EVENT, Unit> 事件处理
         * @param onTransition Function1<STATE, Unit> 监听状态转移
         * @return  StateMachineSwitch<STATE, EVENT> 状态机实例
         */
        fun <STATE : Any, EVENT : Any> create(
            name: String,
            coroutineDispatcher: CoroutineDispatcher,
            initialState: STATE,
            onEvent: (EVENT) -> Unit,
            onTransition: (STATE, STATE) -> Unit
        ): StateMachine<STATE, EVENT> {
            return StateMachine(name, coroutineDispatcher, initialState, onEvent, onTransition)
        }
    }

}

