package com.nxg.androidsample.main

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.nxg.androidsample.R
import com.nxg.androidsample.main.data.Banner
import com.nxg.androidsample.main.data.GridMenu
import com.nxg.androidsample.main.data.NavFunction
import com.nxg.mvvm.viewmodel.BaseSharedAndroidViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(application: Application) : BaseSharedAndroidViewModel(application) {

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

    private val _bannerStateFlow =
        MutableStateFlow(mutableListOf<Banner>())
    val bannerStateFlow = _bannerStateFlow.asStateFlow()


    private fun onBannerStateFlow(list: MutableList<Banner>) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                _bannerStateFlow.emit(list)
            }
        }
    }

    private val _horizontalGridMenuStateFlow =
        MutableStateFlow(mutableListOf<GridMenu>())
    val horizontalGridMenuStateFlow = _horizontalGridMenuStateFlow.asStateFlow()


    private fun onHorizontalGridMenuStateFlow(list: MutableList<GridMenu>) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                _horizontalGridMenuStateFlow.emit(list)
            }
        }
    }


    init {
        val bannerList = mutableListOf(
            Banner(
                "1",
                "https://res.youpin.mi-img.com/youpinoper/8f6eabc4_ff0c_4c44_b3c6_20edd700ace5.jpeg@base@tag=imgScale&F=webp&h=320&w=750"
            ),
            Banner(
                "2",
                "https://res.youpin.mi-img.com/youpinoper/004547c4_6733_4030_9857_60906e7e9f06.jpeg@base@tag=imgScale&F=webp&h=320&w=750"
            ),
            Banner(
                "3",
                "https://res.youpin.mi-img.com/youpinoper/23e9d50b_3e72_44fa_88f1_f391ce410381.png@base@tag=imgScale&F=webp&h=320&w=750"
            ),
            Banner(
                "4",
                "https://res.youpin.mi-img.com/youpinoper/8b6d166c_bbaa_47f6_b7a3_ec900a7f37f9.jpeg@base@tag=imgScale&F=webp&h=320&w=750"
            ),
            Banner(
                "5",
                "https://res.youpin.mi-img.com/youpinoper/12d73085_4806_4a86_94d0_68e155281dd9.png@base@tag=imgScale&F=webp&h=320&w=750"
            )
        )
        onBannerStateFlow(bannerList)

        val horizontalGridMenu = mutableListOf(
            GridMenu(
                "京东超市",
                "https://m15.360buyimg.com/mobilecms/jfs/t1/175540/24/19329/6842/60ec0b0aEf35f7384/ec560dbf9b82b90b.png!q70.jpg",
                -1
            ),
            GridMenu(
                "数码电器",
                "https://m15.360buyimg.com/mobilecms/jfs/t1/178015/31/13828/6862/60ec0c04Ee2fd63ac/ccf74d805a059a44.png!q70.jpg",
                -1
            ),
            GridMenu(
                "京东新百货",
                "https://m15.360buyimg.com/mobilecms/jfs/t1/54043/33/19389/4660/62b049dbE3b9aef75/2fcd31afd5d702e4.png!q70.jpg",
                -1
            ),
            GridMenu(
                "京东生鲜",
                "https://m15.360buyimg.com/mobilecms/jfs/t1/177902/16/13776/5658/60ec0e71E801087f2/a0d5a68bf1461e6d.png!q70.jpg",
                -1
            ),
            GridMenu(
                "京东国际",
                "https://m15.360buyimg.com/mobilecms/jfs/t1/178015/31/13828/6862/60ec0c04Ee2fd63ac/ccf74d805a059a44.png!q70.jpg",
                -1
            ),
            GridMenu(
                "京东拍卖",
                "https://m15.360buyimg.com/mobilecms/jfs/t1/178015/31/13828/6862/60ec0c04Ee2fd63ac/ccf74d805a059a44.png!q70.jpg",
                -1
            ),
            GridMenu(
                "看病购药",
                "https://m15.360buyimg.com/mobilecms/jfs/t1/178015/31/13828/6862/60ec0c04Ee2fd63ac/ccf74d805a059a44.png!q70.jpg",
                -1
            ),
            GridMenu(
                "玩3C",
                "https://m15.360buyimg.com/mobilecms/jfs/t1/178015/31/13828/6862/60ec0c04Ee2fd63ac/ccf74d805a059a44.png!q70.jpg",
                -1
            ),
            GridMenu(
                "沃尔玛",
                "https://m15.360buyimg.com/mobilecms/jfs/t1/178015/31/13828/6862/60ec0c04Ee2fd63ac/ccf74d805a059a44.png!q70.jpg",
                -1
            ),
            GridMenu(
                "京东国际",
                "https://m15.360buyimg.com/mobilecms/jfs/t1/178015/31/13828/6862/60ec0c04Ee2fd63ac/ccf74d805a059a44.png!q70.jpg",
                -1
            ),
            GridMenu(
                "京东拍卖",
                "https://m15.360buyimg.com/mobilecms/jfs/t1/178015/31/13828/6862/60ec0c04Ee2fd63ac/ccf74d805a059a44.png!q70.jpg",
                -1
            ),
            GridMenu(
                "看病购药",
                "https://m15.360buyimg.com/mobilecms/jfs/t1/178015/31/13828/6862/60ec0c04Ee2fd63ac/ccf74d805a059a44.png!q70.jpg",
                -1
            ),
            GridMenu(
                "玩3C",
                "https://m15.360buyimg.com/mobilecms/jfs/t1/178015/31/13828/6862/60ec0c04Ee2fd63ac/ccf74d805a059a44.png!q70.jpg",
                -1
            ),
            GridMenu(
                "沃尔玛",
                "https://m15.360buyimg.com/mobilecms/jfs/t1/178015/31/13828/6862/60ec0c04Ee2fd63ac/ccf74d805a059a44.png!q70.jpg",
                -1
            ),
            GridMenu(
                "数码电器1",
                "https://m15.360buyimg.com/mobilecms/jfs/t1/178015/31/13828/6862/60ec0c04Ee2fd63ac/ccf74d805a059a44.png!q70.jpg",
                -1
            ),
            GridMenu(
                "数码电器2",
                "https://m15.360buyimg.com/mobilecms/jfs/t1/178015/31/13828/6862/60ec0c04Ee2fd63ac/ccf74d805a059a44.png!q70.jpg",
                -1
            ),
            GridMenu(
                "数码电器3",
                "https://m15.360buyimg.com/mobilecms/jfs/t1/178015/31/13828/6862/60ec0c04Ee2fd63ac/ccf74d805a059a44.png!q70.jpg",
                -1
            ),
            GridMenu(
                "数码电器",
                "https://m15.360buyimg.com/mobilecms/jfs/t1/178015/31/13828/6862/60ec0c04Ee2fd63ac/ccf74d805a059a44.png!q70.jpg",
                -1
            ),
            GridMenu(
                "数码电器4",
                "https://m15.360buyimg.com/mobilecms/jfs/t1/178015/31/13828/6862/60ec0c04Ee2fd63ac/ccf74d805a059a44.png!q70.jpg",
                -1
            ),
            GridMenu(
                "数码电器5",
                "https://m15.360buyimg.com/mobilecms/jfs/t1/178015/31/13828/6862/60ec0c04Ee2fd63ac/ccf74d805a059a44.png!q70.jpg",
                -1
            ),
        )
        onHorizontalGridMenuStateFlow(horizontalGridMenu)
    }

}