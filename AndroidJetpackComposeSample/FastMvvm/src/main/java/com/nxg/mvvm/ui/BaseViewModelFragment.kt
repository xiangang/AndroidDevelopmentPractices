package com.nxg.mvvm.ui

import androidx.annotation.LayoutRes
import com.nxg.mvvm.applicationViewModels
import com.nxg.mvvm.viewmodel.BaseSharedAndroidViewModel

open class BaseViewModelFragment : BaseFragment{

    constructor() : super()

    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    //作用域范围为Application的共享ShareViewModels
    val mBaseSharedAndroidViewModel: BaseSharedAndroidViewModel by applicationViewModels()

}