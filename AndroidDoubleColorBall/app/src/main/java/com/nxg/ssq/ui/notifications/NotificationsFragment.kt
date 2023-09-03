package com.nxg.ssq.ui.notifications

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.nxg.srollview.ScrollView
import com.nxg.ssq.R
import com.nxg.ssq.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {

    companion object {
        private const val TAG = "NotificationsFragment"
    }

    private lateinit var notificationsViewModel: NotificationsViewModel
    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textNotifications
        notificationsViewModel.text.observe(viewLifecycleOwner, {
            textView.text = it
        })

        initScrollView()
        return root
    }


    private fun initScrollView() {
        val scrollView: ScrollView = binding.scrollView
        val scrollView2: ScrollView = binding.scrollView2
        val scrollView3: ScrollView = binding.scrollView3
        val scrollView4: ScrollView = binding.scrollView4
        val scrollView5: ScrollView = binding.scrollView5
        val scrollView6: ScrollView = binding.scrollView6
        val scrollView7: ScrollView = binding.scrollView7
        val configurationBlue = ScrollView.build {
            scrollAnimatorDuration = 3000L
            itemLayoutId = R.layout.item_text_blue
        }
        val configurationRed = ScrollView.build {
            scrollAnimatorDuration = 3000L
            itemLayoutId = R.layout.item_text_red
        }
        scrollView.setConfiguration(configurationRed)
        scrollView2.setConfiguration(configurationRed)
        scrollView3.setConfiguration(configurationRed)
        scrollView4.setConfiguration(configurationRed)
        scrollView5.setConfiguration(configurationRed)
        scrollView6.setConfiguration(configurationRed)
        scrollView7.setConfiguration(configurationBlue)

        notificationsViewModel.listText.observe(viewLifecycleOwner, {
            Log.i(TAG, "initScrollView: $it")
            scrollView.setData(it)
        })

        notificationsViewModel.listText2.observe(viewLifecycleOwner, {
            Log.i(TAG, "initScrollView: $it")
            scrollView2.setData(it)
        })

        notificationsViewModel.listText3.observe(viewLifecycleOwner, {
            Log.i(TAG, "initScrollView: $it")
            scrollView3.setData(it)
        })

        notificationsViewModel.listText4.observe(viewLifecycleOwner, {
            Log.i(TAG, "initScrollView: $it")
            scrollView4.setData(it)
        })

        notificationsViewModel.listText5.observe(viewLifecycleOwner, {
            Log.i(TAG, "initScrollView: $it")
            scrollView5.setData(it)
        })

        notificationsViewModel.listText6.observe(viewLifecycleOwner, {
            Log.i(TAG, "initScrollView: $it")
            scrollView6.setData(it)
        })
        notificationsViewModel.listText7.observe(viewLifecycleOwner, {
            Log.i(TAG, "initScrollView: $it")
            scrollView7.setData(it)
        })

        binding.buttonStart.setOnClickListener {
            Log.i(TAG, "initScrollView: start")
            notificationsViewModel.loadRandomText()
            scrollView.start()
            scrollView2.start(100)
            scrollView3.start(200)
            scrollView4.start(300)
            scrollView5.start(400)
            scrollView6.start(500)
            scrollView7.start(600)
        }
        notificationsViewModel.loadRandomText()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}