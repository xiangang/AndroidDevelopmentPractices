package com.nxg.mvvm

import android.app.Application
import androidx.annotation.NonNull
import androidx.lifecycle.*

/**
 * 提供Application作用域的ViewModel
 */
open class BaseViewModelApplication : Application(), ViewModelStoreOwner,
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

    override fun getViewModelStore(): ViewModelStore {
        return mViewModelStore
    }

    override fun getDefaultViewModelProviderFactory(): ViewModelProvider.Factory {
        return mViewModelProviderFactory
    }
}