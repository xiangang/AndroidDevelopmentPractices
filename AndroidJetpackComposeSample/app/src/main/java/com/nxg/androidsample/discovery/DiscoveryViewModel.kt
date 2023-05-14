package com.nxg.androidsample.discovery

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nxg.androidsample.R
import com.nxg.androidsample.main.MainFragmentDirections
import com.nxg.androidsample.main.data.NavFunction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DiscoveryViewModel(application: Application) : AndroidViewModel(application) {

    private val _navFunctionMapStateFlow =
        MutableStateFlow(mapOf<String, List<NavFunction>>())
    val navFunctionMapStateFlow = _navFunctionMapStateFlow.asStateFlow()

    private fun onNavFunctionMapStateFlow(map: Map<String, List<NavFunction>>) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                _navFunctionMapStateFlow.emit(map)
            }
        }
    }

    init {
        val navFunctionList = mutableListOf<NavFunction>()
        navFunctionList.add(
            NavFunction(
                "UI",
                "Jetpack Compose",
                R.drawable.ic_dolphin,
                "",
                MainFragmentDirections.actionMainFragmentToNuiNavGraph(),
            )
        )
        navFunctionList.add(
            NavFunction(
                "音视频",
                "FFmpeg",
                R.drawable.ic_dog,
                "",
                MainFragmentDirections.actionMainFragmentToAvNavGraph(),
            )
        )

        navFunctionList.add(
            NavFunction(
                "音视频",
                "WebRTC推拉流",
                R.drawable.ic_dog,
                "",
                MainFragmentDirections.actionMainFragmentToLiveStreamingFragment(),
            )
        )

        navFunctionList.add(
            NavFunction(
                "音视频",
                "WebRTC视频通话",
                R.drawable.ic_dog,
                "",
                MainFragmentDirections.actionMainFragmentToVideoCallFragment(),
            )
        )

        val grouped = navFunctionList.groupBy { it.functionGroupName }
        onNavFunctionMapStateFlow(grouped)
    }

}