package com.nxg.mvvm.ui

import androidx.annotation.LayoutRes
import com.nxg.mvvm.applicationViewModels
import com.nxg.mvvm.viewmodel.ApplicationShareViewModel

/**
 * 提供Application作用域的ViewModel
 */
abstract class BaseViewModelFragment : BaseFragment{

    constructor() : super()

    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    //作用域范围为Application的共享ShareViewModels
    val mApplicationShareViewModel: ApplicationShareViewModel by applicationViewModels()



}