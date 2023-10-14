package com.nxg.commonui.compose

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import com.nxg.commonui.BuildConfig

class Ref(var value: Int)

// 注意，此处的 inline 会使下列函数实际上直接内联到调用处
// 以确保 logging 仅在原始调用位置被调用
@Composable
inline fun LogCompositions(tag: String, msg: String) {
    if (BuildConfig.DEBUG) {
        val ref = remember { Ref(0) }
        SideEffect { ref.value++ }
        Log.d(tag, "Compositions: $msg ${ref.value}")
    }
}