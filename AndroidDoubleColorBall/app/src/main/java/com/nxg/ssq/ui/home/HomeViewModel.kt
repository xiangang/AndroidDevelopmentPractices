package com.nxg.ssq.ui.home

import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.random.Random

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text


    private val _listNumber = MutableLiveData<List<NumberBean>>()
    val listNumber: LiveData<List<NumberBean>> = _listNumber

    private fun createNumber(until: Int): List<NumberBean> {
        val numberList = mutableListOf<NumberBean>()
        for (i in 0..until) {
            val num = Random(SystemClock.elapsedRealtimeNanos()).nextInt(until)
            numberList.add(
                NumberBean(
                    if (num < 10) "0$num" else {
                        num.toString()
                    }
                )
            )
        }
        return numberList
    }

    fun refreshNum(){
        _listNumber.value =createNumber(33)
    }
}