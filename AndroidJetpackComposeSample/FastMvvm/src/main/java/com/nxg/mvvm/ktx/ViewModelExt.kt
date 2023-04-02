package com.nxg.mvvm.ktx

import kotlinx.coroutines.*

/**
 * ViewModel扩展方法：启动协程
 * @param coroutineScope 协程作用域
 * @param block 协程逻辑
 * @param onError 错误回调方法
 * @param onComplete 完成回调方法
 */
fun launchExceptionHandler(
    coroutineScope: CoroutineScope,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    block: suspend CoroutineScope.() -> Unit,
    onError: (e: Throwable) -> Unit = { e: Throwable -> e.printStackTrace() },
    onComplete: () -> Unit = {}
) {
    coroutineScope.launch(
        context = dispatcher +
                //除了CancellationException，其它子协程处理不了的异常都会有Root协程处理
                CoroutineExceptionHandler { _, throwable ->
                    run {
                        // 这里统一处理错误
                        //ExceptionUtil.catchException(throwable)
                        onError(throwable)
                    }
                },
        block = {
            try {
                block.invoke(this)
            } finally {
                onComplete()
            }
        }
    )
}
