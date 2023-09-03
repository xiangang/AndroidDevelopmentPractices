package com.nxg.setting.component.privacy

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nxg.setting.component.R

class PrivacyFragment : Fragment() {

    companion object {
        fun newInstance() = PrivacyFragment()
    }

    private lateinit var viewModel: PrivacyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.privacy_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PrivacyViewModel::class.java)
        // TODO: Use the ViewModel
    }

}