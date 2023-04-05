package com.alexvt.wordgame.viewui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
                text = " • update 2",
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
        text = "2. Pick letters in order as in your word, except diagonally.",
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

    Text(
        text = "Computer Difficulty",
        color = Color(uiState.theme.color.text.normal),
        fontSize = uiState.theme.font.size.normal.sp,
        fontFamily = Fonts.NotoSans.get(),
        modifier = Modifier.fillMaxWidth(),
    )
    val difficultyButtons = remember { listOf("Easy", "Medium", "Hard", "Ultra") }
    Row(
        Modifier.height(45.dp).offset((-3).dp, 0.dp).fillMaxWidth()
            .offset(3.dp, 0.dp) // compensating offsets
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
