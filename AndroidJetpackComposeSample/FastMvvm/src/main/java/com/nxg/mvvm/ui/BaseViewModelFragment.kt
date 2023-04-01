package com.nxg.mvvm.ui

import com.nxg.mvvm.applicationViewModels
import com.nxg.mvvm.viewmodel.BaseSharedAndroidViewModel

open class BaseViewModelFragment : BaseFragment() {

    //作用域范围为Application的共享ShareViewModels
    val mBaseSharedAndroidViewModel: BaseSharedAndroidViewModel by applicationViewModels()

}