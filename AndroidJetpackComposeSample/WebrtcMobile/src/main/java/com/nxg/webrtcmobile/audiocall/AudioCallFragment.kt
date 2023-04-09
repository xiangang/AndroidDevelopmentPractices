package com.nxg.webrtcmobile.audiocall

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nxg.webrtcmobile.R
import com.nxg.webrtcmobile.h264.AudioCallViewModel

class AudioCallFragment : Fragment() {

    companion object {
        fun newInstance() = AudioCallFragment()
    }

    private lateinit var viewModel: AudioCallViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.audio_call_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AudioCallViewModel::class.java)
        // TODO: Use the ViewModel
    }

}