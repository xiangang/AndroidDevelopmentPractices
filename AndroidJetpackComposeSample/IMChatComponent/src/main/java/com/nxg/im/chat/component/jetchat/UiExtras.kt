/*
 * Copyright 2020 The Android Open Source Project
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

package com.nxg.im.chat.component.jetchat

import android.os.SystemClock
import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import github.leavesczy.matisse.MediaResource
import github.leavesczy.matisse.MimeType

@Composable
fun FunctionalityNotAvailablePopup(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Text(
                text = "Functionality not available \uD83D\uDE48",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "CLOSE")
            }
        }
    )
}

private inline fun Modifier.clickableLimit(
    indication: Indication?,
    interactionSource: MutableInteractionSource,
    minDuration: Long,
    crossinline onClick: () -> Unit
): Modifier = composed {
    var lastClickTime by remember {
        mutableLongStateOf(value = 0L)
    }
    clickable(
        indication = indication,
        interactionSource = interactionSource
    ) {
        val currentTimeMillis = SystemClock.elapsedRealtime()
        if (currentTimeMillis - lastClickTime > minDuration) {
            lastClickTime = currentTimeMillis
            onClick()
        }
    }
}

private const val MIN_DURATION = 300L

internal inline fun Modifier.clickableLimit(
    minDuration: Long = MIN_DURATION,
    crossinline onClick: () -> Unit
): Modifier = composed {
    clickableLimit(
        indication = LocalIndication.current,
        interactionSource = remember { MutableInteractionSource() },
        minDuration = minDuration,
        onClick = onClick
    )
}

internal inline fun Modifier.clickableNoRippleLimit(
    minDuration: Long = MIN_DURATION,
    crossinline onClick: () -> Unit
): Modifier = composed {
    clickableLimit(
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
        minDuration = minDuration,
        onClick = onClick
    )
}

internal fun Modifier.clickableNoRipple(
    onClick: () -> Unit
): Modifier =
    composed {
        clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() },
            onClick = onClick
        )
    }

internal val MimeType.isImage: Boolean
    get() = type.startsWith(prefix = "image/")

internal val MimeType.isVideo: Boolean
    get() = type.startsWith(prefix = "video/")

internal val MediaResource.isVideo: Boolean
    get() = mimeType.startsWith(prefix = "video/")