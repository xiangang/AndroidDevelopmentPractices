// MIT License
//
// Copyright (c) 2022 被风吹过的夏天
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

package com.nxg.im.chat.component.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.nxg.im.chat.R
import com.nxg.im.commonui.components.dialog.SimpleDialog

/**
 * ShowOpenGPSDialog
 * @author 被风吹过的夏天
 * @email developer_melody@163.com
 * @github: https://github.com/TheMelody/OmniMap
 * created 2022/10/10 15:31
 */
@Composable
internal fun ShowOpenGPSDialog(onPositiveClick: () -> Unit, onDismiss: () -> Unit) {
    SimpleDialog(
        positiveButtonResId = R.string.gd_map_location_gps_dialog_ok,
        negativeButtonResId = R.string.gd_map_location_gps_dialog_cancel,
        content = stringResource(id = R.string.gd_map_location_gps_no_open),
        onPositiveClick = onPositiveClick,
        onNegativeClick = onDismiss,
        onDismiss = onDismiss
    )
}