package com.nxg.webrtcmobile.livestreaming

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nxg.webrtcmobile.R

class LiveStreamingFragment : Fragment() {

    companion object {
        fun newInstance() = LiveStreamingFragment()
    }

    private lateinit var viewModel: LiveStreamingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.live_streaming_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LiveStreamingViewModel::class.java)
        // TODO: Use the ViewModel
    }

}