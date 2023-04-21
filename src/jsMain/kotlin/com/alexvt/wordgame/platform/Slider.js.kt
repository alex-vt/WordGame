package com.alexvt.wordgame.platform

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.SliderDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.toSize
import kotlin.math.roundToInt

/**
 * On this platform there are troubles with androidx.compose.material.Slider inputs.
 * The solution is interception of inputs with an overlaying transparent widget.
 */
@OptIn(ExperimentalComposeUiApi::class)
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
) = Box(modifier) {
    androidx.compose.material.Slider(
        sliderValueState.value,
        onValueChange = {},
        Modifier.matchParentSize(),
        enabled = false,
        valueRange,
        steps,
        onValueChangeFinished = {},
        interactionSource = remember { MutableInteractionSource() },
        colors = SliderDefaults.colors(
            thumbColor = thumbColor,
            disabledThumbColor = thumbColor,
            activeTrackColor = activeTrackColor,
            disabledActiveTrackColor = activeTrackColor,
            inactiveTrackColor = inactiveTrackColor,
            disabledInactiveTrackColor = inactiveTrackColor,
            activeTickColor = activeTickColor,
            disabledActiveTickColor = activeTickColor,
            inactiveTickColor = inactiveTickColor,
            disabledInactiveTickColor = inactiveTickColor,
        ),
    )
    var sliderSize by remember { mutableStateOf(Size(1f, 1f)) }
    Box(
        Modifier.fillMaxSize().onGloballyPositioned { coordinates ->
            sliderSize = coordinates.size.toSize()
        }.onPointerEvent(PointerEventType.Move) { event ->
            event.changes.filter { change -> change.pressed }.forEach { change ->
                sliderValueState.value = change.getNewSliderValue(sliderSize, valueRange, steps)
                onValueChange(sliderValueState.value)
            }
        }.onPointerEvent(PointerEventType.Release) { event ->
            event.changes.forEach { change ->
                sliderValueState.value = change.getNewSliderValue(sliderSize, valueRange, steps)
                onValueChangeFinished?.invoke()
            }
        }
    ) { }
}

private fun PointerInputChange.getNewSliderValue(
    sliderSize: Size,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
): Float {
    val normalizedSliderValue = getNormalisedSliderValue(
        sliderSize, clickPosition = position
    )
    val valueRangeSize = valueRange.endInclusive - valueRange.start
    val step = (valueRangeSize / (steps - 1)).roundToInt()
    val newValue =
        valueRange.start + normalizedSliderValue * valueRangeSize
    return ((newValue / step).roundToInt() * step).toFloat()
}

private fun getNormalisedSliderValue(sliderSize: Size, clickPosition: Offset): Float {
    // Slider is considered horizontal, with its height equal to thumb diameter.
    // Therefore, track length is slider width minus slider height. Track offset is 0.5 thumb size.
    val trackLength = sliderSize.width - sliderSize.height
    val trackOffset = sliderSize.height / 2
    val clickPositionOnTrack = clickPosition.x - trackOffset
    return (clickPositionOnTrack / trackLength).coerceIn(0f, 1f)
}