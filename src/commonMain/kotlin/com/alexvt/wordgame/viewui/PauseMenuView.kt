package com.alexvt.wordgame.viewui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.alexvt.wordgame.AppDependencies
import com.alexvt.wordgame.platform.browseLink
import com.alexvt.wordgame.platform.getImageBitmapFromResources
import com.alexvt.wordgame.viewmodel.PauseMenuViewModel
import com.alexvt.wordgame.viewui.common.Fonts
import com.alexvt.wordgame.viewui.common.WindowLifecycleEnvironment
import moe.tlaster.precompose.ui.viewModel
import kotlin.math.roundToInt

@Composable
fun PauseMenuView(dependencies: AppDependencies, lifecycle: WindowLifecycleEnvironment) {
    val viewModel = viewModel {
        PauseMenuViewModel(
            dependencies.pauseMenuViewModelUseCases, lifecycle.backgroundCoroutineDispatcher,
        )
    }
    val uiState by viewModel.getUiStateFlow().collectAsState()

    Box(Modifier.fillMaxWidth()) {
        Row {
            Text(
                text = "Word Game",
                color = Color(uiState.theme.color.text.normal),
                fontSize = uiState.theme.font.size.small.sp,
                fontFamily = Fonts.NotoSans.get(),
            )
            Text(
                text = " • update 6",
                color = Color(uiState.theme.color.text.dim),
                fontSize = uiState.theme.font.size.small.sp,
                fontFamily = Fonts.NotoSans.get(),
            )
        }
        val uriHandler = LocalUriHandler.current
        Text(
            text = "on GitHub",
            color = Color(uiState.theme.color.text.accent),
            fontSize = uiState.theme.font.size.small.sp,
            textDecoration = TextDecoration.Underline,
            fontFamily = Fonts.NotoSans.get(),
            modifier = Modifier.align(Alignment.TopEnd)
                .clickable {
                    browseLink(uriHandler, "https://github.com/alex-vt/WordGame")
                },
        )
    }
    Spacer(Modifier.height(12.dp))

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Theme:",
            color = Color(uiState.theme.color.text.dim),
            fontSize = uiState.theme.font.size.small.sp,
            fontFamily = Fonts.NotoSans.get(),
        )
        Spacer(Modifier.width(8.dp))
        Row(
            Modifier.height(30.dp).fillMaxWidth().offset(3.dp, 0.dp) // compensating offsets
        ) {
            val cornerRadius = 5.dp
            uiState.colorThemeOptions.forEachIndexed { themeIndex, colorThemeOption ->
                OutlinedButton(
                    modifier = Modifier.offset((-1 * themeIndex).dp, 0.dp)
                        .weight(1f)
                        .zIndex(if (colorThemeOption.isSelected) 1f else 0f),
                    onClick = {
                        viewModel.onColorThemeSelection(themeIndex)
                    },
                    shape = when (themeIndex) {
                        // left outer button
                        0 -> RoundedCornerShape(
                            topStart = cornerRadius,
                            topEnd = 0.dp,
                            bottomStart = cornerRadius,
                            bottomEnd = 0.dp
                        )
                        // right outer button
                        uiState.colorThemeOptions.size - 1 -> RoundedCornerShape(
                            topStart = 0.dp,
                            topEnd = cornerRadius,
                            bottomStart = 0.dp,
                            bottomEnd = cornerRadius
                        )
                        // middle button
                        else -> RoundedCornerShape(
                            topStart = 0.dp,
                            topEnd = 0.dp,
                            bottomStart = 0.dp,
                            bottomEnd = 0.dp
                        )
                    },
                    border = BorderStroke(
                        1.dp,
                        Color(
                            with(uiState.theme.color.border) {
                                if (colorThemeOption.isSelected) selected else unselected
                            }
                        )
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        backgroundColor = Color(
                            with(uiState.theme.color.background) {
                                if (colorThemeOption.isSelected) clickable else unselected
                            }
                        )
                    ),
                    contentPadding = PaddingValues(0.dp),
                ) {
                    Box(
                        modifier = Modifier.size(16.dp).background(
                            color = Color(colorThemeOption.backgroundColor),
                            shape = RoundedCornerShape(8.dp),
                        ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "T",
                            color = Color(colorThemeOption.textColor),
                            fontSize = uiState.theme.font.size.small.sp,
                            fontFamily = Fonts.NotoSans.get(),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
    Spacer(Modifier.height(8.dp))

    Text(
        text = "How to play:",
        color = Color(uiState.theme.color.text.dim),
        fontSize = uiState.theme.font.size.small.sp,
        fontFamily = Fonts.NotoSans.get(),
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.height(4.dp))
    Text(
        text = "1. Choose a place on the board. Type there a letter that's part of a word - read in any directions, like in a maze.",
        color = Color(uiState.theme.color.text.dim),
        fontSize = uiState.theme.font.size.small.sp,
        fontFamily = Fonts.NotoSans.get(),
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.height(4.dp))
    Text(
        text = "2. Pick letters in order as in your word.",
        color = Color(uiState.theme.color.text.dim),
        fontSize = uiState.theme.font.size.small.sp,
        fontFamily = Fonts.NotoSans.get(),
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.height(4.dp))
    Text(
        text = "3. Press OK to end your move, or Del to clear it and start from 1.",
        color = Color(uiState.theme.color.text.dim),
        fontSize = uiState.theme.font.size.small.sp,
        fontFamily = Fonts.NotoSans.get(),
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.height(4.dp))
    Text(
        text = "4. Singular common nouns are allowed.",
        color = Color(uiState.theme.color.text.dim),
        fontSize = uiState.theme.font.size.small.sp,
        fontFamily = Fonts.NotoSans.get(),
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.height(4.dp))
    Text(
        text = "5. When the board is full, the sum of word lengths defines the winner.",
        color = Color(uiState.theme.color.text.dim),
        fontSize = uiState.theme.font.size.small.sp,
        fontFamily = Fonts.NotoSans.get(),
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.height(16.dp))

    Text(
        text = "New Game Settings",
        color = Color(uiState.theme.color.text.normal),
        fontSize = uiState.theme.font.size.big.sp,
        fontFamily = Fonts.NotoSans.get(),
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.height(4.dp))

    Column {
        data class GameType(
            val icon1path: String,
            val icon2path: String,
            val text: String,
            val subText: String
        )

        val gameTypes = listOf(
            GameType("player", "player", "Player vs Player", "offline"),
            GameType("player", "computer", "Player vs Computer", ""),
            GameType("computer", "computer", "Watch 2 computers", "autoplay"),
        )
        val selectedIndex = uiState.gameTypeSelectionIndex
        val cornerRadius = 5.dp

        gameTypes.forEachIndexed { buttonIndex, gameType ->
            OutlinedButton(
                modifier = when (buttonIndex) {
                    0 ->
                        Modifier.offset(0.dp, 0.dp)
                    else ->
                        Modifier.offset(0.dp, (-1 * buttonIndex).dp)
                }
                    .height(48.dp).fillMaxWidth()
                    .zIndex(if (selectedIndex == buttonIndex) 1f else 0f),
                onClick = {
                    viewModel.onGameTypeSelection(buttonIndex)
                },
                shape = when (buttonIndex) {
                    // top outer button
                    0 -> RoundedCornerShape(
                        topStart = cornerRadius,
                        topEnd = cornerRadius,
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp
                    )
                    // bottom outer button
                    gameTypes.size - 1 -> RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomStart = cornerRadius,
                        bottomEnd = cornerRadius
                    )
                    // middle button
                    else -> RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp
                    )
                },
                border = BorderStroke(
                    1.dp,
                    Color(
                        with(uiState.theme.color.border) {
                            if (selectedIndex == buttonIndex) selected else unselected
                        }
                    )
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    backgroundColor = Color(
                        with(uiState.theme.color.background) {
                            if (selectedIndex == buttonIndex) clickable else unselected
                        }
                    )
                ),
                contentPadding = PaddingValues(0.dp),
            ) {
                Row(Modifier.fillMaxWidth().padding(1.dp)) {
                    Image(
                        bitmap = getImageBitmapFromResources(res = gameType.icon1path),
                        contentDescription = "",
                        modifier = Modifier.size(48.dp).alpha(
                            if (selectedIndex == buttonIndex) 1f else 0.6f
                        )
                    )
                    Image(
                        bitmap = getImageBitmapFromResources(res = gameType.icon2path),
                        contentDescription = "",
                        modifier = Modifier.size(48.dp).offset((-5).dp, 0.dp).alpha(
                            if (selectedIndex == buttonIndex) 1f else 0.6f
                        ).graphicsLayer(scaleX = -1f) // flipped
                    )
                    Column(Modifier.align(Alignment.CenterVertically)) {
                        Text(
                            text = gameType.text,
                            fontSize = uiState.theme.font.size.normal.sp,
                            color = Color(
                                with(uiState.theme.color.text) {
                                    if (selectedIndex == buttonIndex) normal else inactive
                                }
                            ),
                            fontFamily = Fonts.NotoSans.get(),
                        )
                        if (gameType.subText.isNotBlank()) {
                            Text(
                                text = gameType.subText,
                                fontSize = uiState.theme.font.size.small.sp,
                                color = Color(
                                    with(uiState.theme.color.text) {
                                        if (selectedIndex == buttonIndex) dim else dimInactive
                                    }
                                ),
                                fontFamily = Fonts.NotoSans.get(),
                            )
                        }
                    }
                }
            }
        }
    }
    Spacer(Modifier.height(8.dp))

    Row(
        Modifier.fillMaxWidth().alpha(if (uiState.isComputerDifficultyVisible) 1f else 0f),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Computer Difficulty",
            color = Color(uiState.theme.color.text.normal),
            fontSize = uiState.theme.font.size.normal.sp,
            fontFamily = Fonts.NotoSans.get(),
        )
        val cornerRadius = 5.dp
        OutlinedButton(
            modifier = Modifier.height(26.dp),
            onClick = {
                viewModel.onCustomDifficultyModeClick()
            },
            shape = RoundedCornerShape(cornerRadius),
            border = BorderStroke(
                1.dp,
                Color(
                    with(uiState.theme.color.border) {
                        if (uiState.isComputerDifficultyCustom) selected else unselected
                    }
                )
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                backgroundColor = Color(
                    with(uiState.theme.color.background) {
                        if (uiState.isComputerDifficultyCustom) clickable else unselected
                    }
                )
            ),
            contentPadding = PaddingValues(0.dp),
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 5.dp),
                text = if (uiState.isComputerDifficultyCustom) "Reset" else "Custom",
                fontSize = uiState.theme.font.size.small.sp,
                color = Color(
                    with(uiState.theme.color.text) {
                        if (uiState.isComputerDifficultyCustom) normal else inactive
                    }
                ),
                fontFamily = Fonts.NotoSans.get(),
            )
        }
    }
    if (uiState.isComputerDifficultyCustom) {
        Row(
            Modifier.fillMaxWidth().height(25.dp)
                .alpha(if (uiState.isComputerDifficultyVisible) 1f else 0f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.width(115.dp),
                text = "Vocabulary",
                fontSize = uiState.theme.font.size.small.sp,
                color = Color(uiState.theme.color.text.dim),
                fontFamily = Fonts.NotoSans.get(),
            )
            val sliderPosition = remember {
                mutableStateOf(uiState.customDifficultyVocabularySliderValue.toFloat())
            }
            com.alexvt.wordgame.platform.Slider(
                // androidx.compose.material.Slider w. platform fix
                modifier = Modifier.weight(1f),
                sliderValueState = sliderPosition,
                valueRange = 5f..100f,
                steps = 20,
                onValueChange = {
                    // fix for steps not being respected
                    val step = 5f
                    sliderPosition.value = (it / step).roundToInt() * step
                },
                onValueChangeFinished = {
                    viewModel.onCustomDifficultyVocabularySelection(
                        maxVocabularySliderValue = sliderPosition.value.toInt()
                    )
                },
                thumbColor = Color(uiState.theme.color.border.selected),
                activeTrackColor = Color(uiState.theme.color.border.neutral),
                inactiveTrackColor = Color(uiState.theme.color.background.unselected),
                activeTickColor = Color.Transparent,
                inactiveTickColor = Color.Transparent,
            )
            Text(
                modifier = Modifier.width(40.dp).padding(start = 2.dp),
                text = "${sliderPosition.value.toInt()}%",
                fontSize = uiState.theme.font.size.small.sp,
                color = Color(uiState.theme.color.text.normal),
                fontFamily = Fonts.NotoSans.get(),
            )
        }
        Row(
            Modifier.fillMaxWidth().height(25.dp)
                .alpha(if (uiState.isComputerDifficultyVisible) 1f else 0f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.width(115.dp),
                text = "Max word length",
                fontSize = uiState.theme.font.size.small.sp,
                color = Color(uiState.theme.color.text.dim),
                fontFamily = Fonts.NotoSans.get(),
            )
            val sliderPosition = remember {
                mutableStateOf(uiState.customDifficultyMaxWordLengthSliderValue.toFloat())
            }
            com.alexvt.wordgame.platform.Slider(
                // androidx.compose.material.Slider w. platform fix
                modifier = Modifier.weight(1f),
                sliderValueState = sliderPosition,
                valueRange = 3f..15f,
                steps = 13,
                onValueChange = {
                    sliderPosition.value = it
                },
                onValueChangeFinished = {
                    viewModel.onCustomDifficultyWordLengthSelection(
                        maxWordLengthSliderValue = sliderPosition.value.toInt()
                    )
                },
                thumbColor = Color(uiState.theme.color.border.selected),
                activeTrackColor = Color(uiState.theme.color.border.neutral),
                inactiveTrackColor = Color(uiState.theme.color.background.unselected),
                activeTickColor = Color.Transparent,
                inactiveTickColor = Color.Transparent,
            )
            Text(
                modifier = Modifier.width(40.dp).padding(start = 2.dp),
                text = "${sliderPosition.value.toInt()}",
                fontSize = uiState.theme.font.size.small.sp,
                color = Color(uiState.theme.color.text.normal),
                fontFamily = Fonts.NotoSans.get(),
            )
        }
        Spacer(Modifier.height(3.dp))
    } else {
        val difficultyButtons = remember { listOf("Easy", "Medium", "Hard", "Ultra") }
        Row(
            Modifier.height(45.dp).offset((-3).dp, 0.dp).fillMaxWidth()
                .offset(3.dp, 0.dp) // compensating offsets
                .alpha(if (uiState.isComputerDifficultyVisible) 1f else 0f)
        ) {
            val selectedIndex = uiState.computerDifficultySelectionIndex
            val cornerRadius = 5.dp

            difficultyButtons.forEachIndexed { buttonIndex, buttonText ->
                OutlinedButton(
                    modifier = Modifier.offset((-1 * buttonIndex).dp, 0.dp)
                        .weight(buttonText.length + 2f) // +constant for lesser bias to proportion
                        .zIndex(if (selectedIndex == buttonIndex) 1f else 0f),
                    onClick = {
                        viewModel.onDifficultySelection(buttonIndex)
                    },
                    shape = when (buttonIndex) {
                        // left outer button
                        0 -> RoundedCornerShape(
                            topStart = cornerRadius,
                            topEnd = 0.dp,
                            bottomStart = cornerRadius,
                            bottomEnd = 0.dp
                        )
                        // right outer button
                        difficultyButtons.size - 1 -> RoundedCornerShape(
                            topStart = 0.dp,
                            topEnd = cornerRadius,
                            bottomStart = 0.dp,
                            bottomEnd = cornerRadius
                        )
                        // middle button
                        else -> RoundedCornerShape(
                            topStart = 0.dp,
                            topEnd = 0.dp,
                            bottomStart = 0.dp,
                            bottomEnd = 0.dp
                        )
                    },
                    border = BorderStroke(
                        1.dp,
                        Color(
                            with(uiState.theme.color.border) {
                                if (selectedIndex == buttonIndex) selected else unselected
                            }
                        )
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        backgroundColor = Color(
                            with(uiState.theme.color.background) {
                                if (selectedIndex == buttonIndex) clickable else unselected
                            }
                        )
                    ),
                    contentPadding = PaddingValues(0.dp),
                ) {
                    Text(
                        text = buttonText,
                        fontSize = uiState.theme.font.size.normal.sp,
                        color = Color(
                            with(uiState.theme.color.text) {
                                if (selectedIndex == buttonIndex) normal else inactive
                            }
                        ),
                        fontFamily = Fonts.NotoSans.get(),
                        modifier = Modifier
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))
    }

    Button(
        onClick = { viewModel.onNewGame() },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color(uiState.theme.color.background.accent)
        ),
        modifier = Modifier,
    ) {
        Text(
            text = "New Game",
            color = Color(uiState.theme.color.text.bright),
            fontSize = uiState.theme.font.size.button.sp,
            textAlign = TextAlign.Center,
            fontFamily = Fonts.NotoSans.get(),
            modifier = Modifier.width(200.dp),
        )
    }
    Spacer(Modifier.height(4.dp))

    Text(
        text = "You are in an unfinished game.",
        color = Color(uiState.theme.color.text.error),
        fontSize = uiState.theme.font.size.small.sp,
        textAlign = TextAlign.Center,
        fontFamily = Fonts.NotoSans.get(),
        modifier = Modifier.alpha(if (uiState.isOngoingGameWarningVisible) 1f else 0f),
    )
    Spacer(Modifier.height(4.dp))

    Button(
        onClick = { viewModel.onBackToGame() },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color(uiState.theme.color.background.clickable),
        ),
        modifier = Modifier,
    ) {
        Text(
            text = "Back to game",
            color = Color(uiState.theme.color.text.normal),
            fontSize = uiState.theme.font.size.button.sp,
            textAlign = TextAlign.Center,
            fontFamily = Fonts.NotoSans.get(),
            modifier = Modifier.width(200.dp),
        )
    }
}
