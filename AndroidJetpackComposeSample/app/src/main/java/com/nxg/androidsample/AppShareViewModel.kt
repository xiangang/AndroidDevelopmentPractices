package com.nxg.androidsample

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * 作用域范围为Application的共享ShareViewModel
 * 使用方法：
 * ```
 * class MyAppCompatActivity : AppCompatActivity() {
 *     val appShareViewModel: AppShareViewModel by applicationViewModels()
 * }
 *
 * class MyFragment : Fragment() {
 *     val appShareViewModel: AppShareViewModel by applicationViewModels()
 * }
 * ```
 */
class AppShareViewModel(application: Application) : AndroidViewModel(application) {


}