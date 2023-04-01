package com.nxg.ffmpegstudy.component.entrypoint

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nxg.ffmpegstudy.component.R

class EntryPointFragment : Fragment() {

    companion object {
        fun newInstance() = EntryPointFragment()
    }

    private lateinit var viewModel: EntryPointViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.entry_point_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(EntryPointViewModel::class.java)
        // TODO: Use the ViewModel
    }

}