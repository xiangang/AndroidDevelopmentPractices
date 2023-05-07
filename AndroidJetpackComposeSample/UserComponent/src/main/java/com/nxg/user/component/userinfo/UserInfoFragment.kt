package com.nxg.user.component.userinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.nxg.mvvm.ktx.viewBinding
import com.nxg.mvvm.logger.SimpleLogger
import com.nxg.mvvm.ui.BaseBusinessFragment
import com.nxg.user.component.R
import com.nxg.user.component.databinding.UserInfoFragmentBinding
import com.nxg.user.component.login.ui.login.LoginViewModel
import com.nxg.user.component.login.ui.login.LoginViewModelFactory

class UserInfoFragment : BaseBusinessFragment(R.layout.user_info_fragment), SimpleLogger {

    private val viewModel: UserInfoViewModel by activityViewModels {
        UserInfoViewModelFactory()
    }

    private val loginViewModel: LoginViewModel by activityViewModels {
        LoginViewModelFactory()
    }

    private val binding by viewBinding(UserInfoFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginViewModel.loginResult.observe(
            viewLifecycleOwner
        ) { loginResult ->
            loginResult.success?.let {
                logger.debug { "it.loginData.user ${it.loginData.user}" }
                binding.tvUsername.text = it.loginData.user.username
            }
        }
    }
}