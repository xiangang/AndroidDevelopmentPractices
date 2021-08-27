package com.nxg.composeplane

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.nxg.composeplane.util.LogUtil
import com.nxg.composeplane.util.SoundPoolUtil
import com.nxg.composeplane.viewmodel.GameViewModel
import kotlinx.coroutines.InternalCoroutinesApi


@InternalCoroutinesApi
class GameLifecycleObserver(gameViewModel: GameViewModel) : DefaultLifecycleObserver {

    private val gameViewModelProxy = gameViewModel

    companion object {
        const val TAG = "GameLifecycleObserver"
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        //初始化声音工具类
        SoundPoolUtil.getInstance(gameViewModelProxy.getApplication())
        LogUtil.printLog(TAG, "onCreate()")
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        SoundPoolUtil.getInstance(gameViewModelProxy.getApplication()).resume()
        LogUtil.printLog(TAG, "onResume()")
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        SoundPoolUtil.getInstance(gameViewModelProxy.getApplication()).pause()
        LogUtil.printLog(TAG, "onDestroy()")
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        SoundPoolUtil.getInstance(gameViewModelProxy.getApplication()).release()
        LogUtil.printLog(TAG, "onDestroy()")
    }

}
