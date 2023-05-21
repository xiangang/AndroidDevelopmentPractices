package com.nxg.mvvm

import android.app.Application
import androidx.annotation.NonNull
import androidx.lifecycle.*

/**
 * 提供Application作用域的ViewModel
 */
abstract class BaseViewModelApplication : Application(), ViewModelStoreOwner,
    HasDefaultViewModelProviderFactory {

    companion object {
        const val TAG = "BaseViewModelApplication"
    }

    private val mViewModelStore: ViewModelStore by lazy {
        ViewModelStore()
    }

    private val mViewModelProviderFactory: ViewModelProvider.Factory by lazy {
        ViewModelProvider.AndroidViewModelFactory.getInstance(this)
    }

    override val defaultViewModelProviderFactory: ViewModelProvider.Factory
        get() = mViewModelProviderFactory
    override val viewModelStore: ViewModelStore
        get() = mViewModelStore

}