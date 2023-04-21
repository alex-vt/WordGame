package com.alexvt.wordgame.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Material Slider.
 * On most platforms it's androidx.compose.material.Slider but with slightly different signature.
 * This is needed to fix crash inducing implementation bugs on experimental platforms.
 * Signature change is needed to avoid compilation bugs on experimental platforms.
 */
@Composable
expect fun Slider(
    sliderValueState: MutableState<Float>,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    thumbColor: Color,
    activeTrackColor: Color,
    inactiveTrackColor: Color,
    activeTickColor: Color,
    inactiveTickColor: Color,
)