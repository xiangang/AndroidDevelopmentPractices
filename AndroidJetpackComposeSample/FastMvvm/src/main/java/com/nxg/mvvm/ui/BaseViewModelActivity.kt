package com.nxg.mvvm.ui

import com.nxg.mvvm.applicationViewModels
import com.nxg.mvvm.viewmodel.ApplicationShareViewModel

/**
 * 提供Application作用域的ViewModel
 */
open class BaseViewModelActivity : BaseActivity() {

    //作用域范围为Application的共享ShareViewModels
    val mApplicationShareViewModel: ApplicationShareViewModel by applicationViewModels()

}