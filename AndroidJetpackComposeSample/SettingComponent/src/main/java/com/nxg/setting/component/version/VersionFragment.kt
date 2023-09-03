package com.nxg.setting.component.version

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nxg.setting.component.R

class VersionFragment : Fragment() {

    companion object {
        fun newInstance() = VersionFragment()
    }

    private lateinit var viewModel: VersionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.version_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(VersionViewModel::class.java)
        // TODO: Use the ViewModel
    }

}