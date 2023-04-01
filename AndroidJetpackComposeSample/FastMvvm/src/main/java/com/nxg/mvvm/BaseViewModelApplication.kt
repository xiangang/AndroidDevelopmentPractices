package com.nxg.mvvm

import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.util.Log
import androidx.annotation.NonNull
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner

open class BaseViewModelApplication : Application(), ViewModelStoreOwner,
    HasDefaultViewModelProviderFactory {

    companion object {
        const val TAG = "BaseViewModelApplication"
    }

    // Lazily recreated from NonConfigurationInstances by getViewModelStore()
    private var mViewModelStore: ViewModelStore? = null
    private var mDefaultFactory: ViewModelProvider.Factory? = null


    /**
     * Returns the [ViewModelStore] associated with this application
     *
     *
     * Overriding this method is no longer supported and this method will be made
     * `final` in a future version of ComponentActivity.
     *
     * @return a `ViewModelStore`
     * @throws IllegalStateException if called before the Activity is attached to the Application
     * instance i.e., before onCreate()
     */
    @NonNull
    @Override
    override fun getViewModelStore(): ViewModelStore {
        ensureViewModelStore()
        return mViewModelStore as ViewModelStore
    }

    /**
     * Application不需要处理配置改变导致的重建
     */
    private fun  /* synthetic access */ensureViewModelStore() {
        if (mViewModelStore == null) {
            mViewModelStore = ViewModelStore()
        }
    }

    override fun getDefaultViewModelProviderFactory(): ViewModelProvider.Factory {
        if (mDefaultFactory == null) {
            mDefaultFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(this)
        }
        return mDefaultFactory as ViewModelProvider.Factory
    }
}