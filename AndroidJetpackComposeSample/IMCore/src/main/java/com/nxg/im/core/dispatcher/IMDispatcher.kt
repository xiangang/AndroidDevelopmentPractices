package com.nxg.im.core.dispatcher

import kotlinx.coroutines.*
import java.util.concurrent.Executors


val IMDispatcher: CoroutineDispatcher = Executors.newSingleThreadExecutor { runnable ->
    Thread(runnable, "SdkDataBaseDispatcher")
}.asCoroutineDispatcher()

val IMCoroutineScope = CoroutineScope(IMDispatcher + SupervisorJob())