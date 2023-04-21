package com.alexvt.wordgame.platform

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * On this platform androidx.compose.material.Slider works just fine
 */
@Composable
actual fun Slider(
    sliderValueState: MutableState<Float>,
    onValueChange: (Float) -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    onValueChangeFinished: (() -> Unit)?,
    thumbColor: Color,
    activeTrackColor: Color,
    inactiveTrackColor: Color,
    activeTickColor: Color,
    inactiveTickColor: Color,
) = androidx.compose.material.Slider(
    sliderValueState.value,
    onValueChange,
    modifier,
    enabled,
    valueRange,
    steps,
    onValueChangeFinished,
    interactionSource = remember { MutableInteractionSource() },
    colors = SliderDefaults.colors(
        thumbColor = thumbColor,
        activeTrackColor = activeTrackColor,
        inactiveTrackColor = inactiveTrackColor,
        activeTickColor = activeTickColor,
        inactiveTickColor = inactiveTickColor,
    ),
)