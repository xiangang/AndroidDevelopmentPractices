package com.nxg.mvvm

import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import kotlin.reflect.KClass

/**
 * Returns a property delegate to access application's [ViewModel],
 * if [factoryProducer] is specified then [ViewModelProvider.Factory]
 * returned by it will be used to create [ViewModel] first time. Otherwise, the BaseViewModelApplication's
 * [com.nxg.mvvm.BaseViewModelApplication.getDefaultViewModelProviderFactory](default factory)
 * will be used.
 *
 * ```
 * class MyAppCompatActivity : AppCompatActivity() {
 *     val viewmodel: MyViewModel by applicationViewModels()
 * }
 * ```
 *
 * This property can be accessed only after this AppCompatActivity is create i.e., after
 * [AppCompatActivity.onCreate()], and access prior to that will result in IllegalArgumentException.
 */
@MainThread
inline fun <reified VM : ViewModel> AppCompatActivity.applicationViewModels(
    noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null
) = createViewModelLazy(VM::class,
    { (applicationContext as BaseViewModelApplication).viewModelStore },
    factoryProducer
        ?: { (applicationContext as BaseViewModelApplication).defaultViewModelProviderFactory })

/**
 * Helper method for creation of [ViewModelLazy], that resolves `null` passed as [factoryProducer]
 * to default factory.
 */
@MainThread
fun <VM : ViewModel> AppCompatActivity.createViewModelLazy(
    viewModelClass: KClass<VM>,
    storeProducer: () -> ViewModelStore,
    factoryProducer: (() -> ViewModelProvider.Factory)? = null
): Lazy<VM> {
    val factoryPromise = factoryProducer ?: {
        (applicationContext as BaseViewModelApplication).defaultViewModelProviderFactory
    }
    return ViewModelLazy(viewModelClass, storeProducer, factoryPromise)
}

/**
 * Returns a property delegate to access application's [ViewModel],
 * if [factoryProducer] is specified then [ViewModelProvider.Factory]
 * returned by it will be used to create [ViewModel] first time. Otherwise, the activity's
 * [com.nxg.mvvm.BaseViewModelApplication.getDefaultViewModelProviderFactory](default factory)
 * will be used.
 *
 * ```
 * class MyFragment : Fragment() {
 *     val viewmodel: MyViewModel by applicationViewModels()
 * }
 * ```
 *
 * This property can be accessed only after this Fragment is attached i.e., after
 * [Fragment.onAttach()], and access prior to that will result in IllegalArgumentException.
 */
@MainThread
inline fun <reified VM : ViewModel> Fragment.applicationViewModels(
    noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null
) = createViewModelLazy(VM::class,
    { (requireActivity().applicationContext as BaseViewModelApplication).viewModelStore },
    factoryProducer
        ?: { (requireActivity().applicationContext as BaseViewModelApplication).defaultViewModelProviderFactory })

/**
 * Helper method for creation of [ViewModelLazy], that resolves `null` passed as [factoryProducer]
 * to default factory.
 */
@MainThread
fun <VM : ViewModel> Fragment.createViewModelLazy(
    viewModelClass: KClass<VM>,
    storeProducer: () -> ViewModelStore,
    factoryProducer: (() -> ViewModelProvider.Factory)? = null
): Lazy<VM> {
    val factoryPromise = factoryProducer ?: {
        (requireActivity().applicationContext as BaseViewModelApplication).defaultViewModelProviderFactory
    }
    return ViewModelLazy(viewModelClass, storeProducer, factoryPromise)
}