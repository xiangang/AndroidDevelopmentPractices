package com.nxg.composeplane.view

import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State

/**
 * 自定义animateInt
 */
@Composable
fun InfiniteTransition.animateInt(
    initialValue: Int,
    targetValue: Int,
    animationSpec: InfiniteRepeatableSpec<Int>
): State<Int> =
    animateValue(initialValue, targetValue, Int.VectorConverter, animationSpec)