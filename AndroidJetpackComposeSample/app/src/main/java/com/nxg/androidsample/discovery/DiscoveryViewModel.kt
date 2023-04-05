package com.nxg.androidsample.discovery

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.nxg.androidsample.R
import com.nxg.androidsample.main.MainFragmentDirections
import com.nxg.androidsample.main.data.Banner
import com.nxg.androidsample.main.data.GridMenu
import com.nxg.androidsample.main.data.NavFunction
import com.nxg.mvvm.viewmodel.BaseSharedAndroidViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DiscoveryViewModel(application: Application) : BaseSharedAndroidViewModel(application) {

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
                MainFragmentDirections.actionNavigationHomeToNuiNavGraph(),
            )
        )
        navFunctionList.add(
            NavFunction(
                "音视频",
                "FFmpeg",
                R.drawable.ic_dog,
                "",
                MainFragmentDirections.actionNavigationHomeToAvNavGraph(),
            )
        )

        val grouped = navFunctionList.groupBy { it.functionGroupName }
        onNavFunctionMapStateFlow(grouped)
    }

}