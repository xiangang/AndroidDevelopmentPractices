package com.nxg.ssq.ui.notifications

import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nxg.srollview.bean.TextBean
import kotlin.random.Random

class NotificationsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }
    val text: LiveData<String> = _text

    private val _listText = MutableLiveData<List<TextBean>>()
    val listText: LiveData<List<TextBean>> = _listText

    private val _listText2 = MutableLiveData<List<TextBean>>()
    val listText2: LiveData<List<TextBean>> = _listText2

    private val _listText3 = MutableLiveData<List<TextBean>>()
    val listText3: LiveData<List<TextBean>> = _listText3

    private val _listText4 = MutableLiveData<List<TextBean>>()
    val listText4: LiveData<List<TextBean>> = _listText4

    private val _listText5 = MutableLiveData<List<TextBean>>()
    val listText5: LiveData<List<TextBean>> = _listText5

    private val _listText6 = MutableLiveData<List<TextBean>>()
    val listText6: LiveData<List<TextBean>> = _listText6

    private val _listText7 = MutableLiveData<List<TextBean>>()
    val listText7: LiveData<List<TextBean>> = _listText7

    private fun createRandomText(until: Int): List<TextBean> {
        val numberList = mutableListOf<TextBean>()
        for (i in 0..5) {
            val num = Random(SystemClock.elapsedRealtimeNanos()).nextInt(until)
            numberList.add(
                TextBean(
                    if (num < 10) "0$num" else {
                        num.toString()
                    }
                )
            )
        }
        return numberList
    }

    fun loadRandomText() {
        _listText.value = createRandomText(33)
        _listText2.value = createRandomText(33)
        _listText3.value = createRandomText(33)
        _listText4.value = createRandomText(33)
        _listText5.value = createRandomText(33)
        _listText6.value = createRandomText(16)
        _listText7.value = createRandomText(16)
    }
}