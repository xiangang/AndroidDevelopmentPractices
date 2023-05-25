/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nxg.androidsample.permissions

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nxg.androidsample.databinding.PermissionFragmentBinding
import com.nxg.mvvm.ui.BaseBusinessFragment
import com.nxg.mvvm.ui.PERMISSIONS_REQUIRED

/**
 * This [Fragment] requests permissions and, once granted, it will navigate to the next fragment
 */
class PermissionsFragment : BaseBusinessFragment() {

    companion object {
        const val TAG = "PermissionsFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate: ")
        // add the storage access permission request for Android 9 and below.
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (!checkSelfPermissions(requireContext())) {
                requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.i(TAG, "onCreateView: ")
        return PermissionFragmentBinding.inflate(inflater, container, false).also {
            if (checkSelfPermissions(requireContext())) {
                doWhenPermissionGranted()
            } else {
                activityResultLauncher.launch(PERMISSIONS_REQUIRED)
            }
        }.root
    }

    override fun doWhenPermissionGranted() {
        //跳转到主界面
       /* findMainActivityNavController().navigate(
            PermissionsFragmentDirections.actionPermissionsFragmentToMainFragment()
        )*/
    }
}