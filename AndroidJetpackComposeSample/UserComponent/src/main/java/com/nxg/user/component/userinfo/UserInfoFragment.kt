package com.nxg.user.component.userinfo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.nxg.mvvm.ktx.viewBinding
import com.nxg.mvvm.logger.SimpleLogger
import com.nxg.mvvm.ui.BaseBusinessFragment
import com.nxg.user.component.R
import com.nxg.user.component.databinding.UserInfoFragmentBinding
import com.nxg.user.component.login.LoginViewModel
import com.nxg.user.component.login.LoginViewModelFactory

class UserInfoFragment : BaseBusinessFragment(R.layout.user_info_fragment), SimpleLogger {

    private val userInfoViewModel: UserInfoViewModel by activityViewModels {
        UserInfoViewModelFactory()
    }

    private val binding by viewBinding(UserInfoFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userInfoViewModel.loginResult.observe(
            viewLifecycleOwner
        ) { loginResult ->
            loginResult.success?.let {
                logger.debug { "it.loginData.user ${it.loginData.user}" }
                binding.tvUsername.text = it.loginData.user.username
            }
        }
    }
}