package com.alexvt.wordgame.viewui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.Pause
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexvt.wordgame.AppDependencies
import com.alexvt.wordgame.platform.browseLink
import com.alexvt.wordgame.platform.getImageBitmapFromResources
import com.alexvt.wordgame.viewmodel.WordGameViewModel
import com.alexvt.wordgame.viewui.common.Fonts
import com.alexvt.wordgame.viewui.common.WindowLifecycleEnvironment
import moe.tlaster.precompose.ui.viewModel

@Composable
fun WordGameView(dependencies: AppDependencies, lifecycle: WindowLifecycleEnvironment) {
    Column(
        Modifier.widthIn(max = 350.dp).heightIn(max = 800.dp).padding(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val viewModel = viewModel {
            WordGameViewModel(
                dependencies.wordGameViewModelUseCases, lifecycle.backgroundCoroutineDispatcher,
            )
        }
        val uiState by viewModel.getUiStateFlow().collectAsState()
        val uriHandler = LocalUriHandler.current

        // Score board
        Row {
            Text(
                text = uiState.player1name + ":",
                color = Color(uiState.theme.color.text.dim),
                fontSize = uiState.theme.font.size.small.sp,
                textAlign = TextAlign.Left,
                fontFamily = Fonts.NotoSans.get(),
                modifier = Modifier.weight(1f),
            )
            Text(
                text = uiState.player2name + ":",
                color = Color(uiState.theme.color.text.dim),
                fontSize = uiState.theme.font.size.small.sp,
                textAlign = TextAlign.Right,
                fontFamily = Fonts.NotoSans.get(),
                modifier = Modifier.weight(1f),
            )
        }
        Box {
            Row {
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
                    uiState.player1words.forEachIndexed { index, word ->
                        Text(
                            text = word,
                            color = Color(uiState.theme.color.text.normal),
                            fontSize = uiState.theme.font.size.small.sp,
                            fontFamily = Fonts.RobotoMono.get(),
                            modifier = Modifier.clickable {
                                browseWord(uriHandler, word)
                            }
                        )
                    }
                }
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    uiState.player2words.forEachIndexed { index, word ->
                        Text(
                            text = word,
                            color = Color(uiState.theme.color.text.normal),
                            fontSize = uiState.theme.font.size.small.sp,
                            fontFamily = Fonts.RobotoMono.get(),
                            modifier = Modifier.clickable {
                                browseWord(uriHandler, word)
                            }
                        )
                    }
                }
            }
            Column(
                Modifier.align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                FloatingActionButton(
                    modifier = Modifier.size(50.dp),
                    backgroundColor = Color(uiState.theme.color.background.clickable),
                    contentColor = Color(uiState.theme.color.text.inactive),
                    onClick = { viewModel.onPause() }
                ) {
                    Icon(
                        Icons.Filled.Pause,
                        contentDescription = "Pause Menu",
                        modifier = Modifier.size(24.dp)
                    )
                }
                if (uiState.isRulesHintVisible) {
                    Icon(
                        Icons.Filled.ExpandLess,
                        contentDescription = "Rules",
                        tint = Color(uiState.theme.color.text.dimInactive),
                        modifier = Modifier.padding(top = 5.dp).size(20.dp),
                    )
                    Text(
                        modifier = Modifier.clickable {
                            viewModel.onPause()
                        },
                        text = "Rules",
                        fontSize = uiState.theme.font.size.normal.sp,
                        color = Color(uiState.theme.color.text.dimInactive),
                        fontFamily = Fonts.NotoSans.get(),
                    )
                }
            }
        }
        Row {
            Text(
                text = uiState.player1score,
                color = Color(uiState.theme.color.text.dim),
                fontSize = uiState.theme.font.size.small.sp,
                textAlign = TextAlign.Left,
                fontFamily = Fonts.NotoSans.get(),
                modifier = Modifier.weight(1f),
            )
            Text(
                text = uiState.player2score,
                color = Color(uiState.theme.color.text.dim),
                fontSize = uiState.theme.font.size.small.sp,
                textAlign = TextAlign.Right,
                fontFamily = Fonts.NotoSans.get(),
                modifier = Modifier.weight(1f),
            )
        }

        // Game board with header / footer messages
        Column(
            Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {

            // Notification message
            Row(
                Modifier.fillMaxWidth().padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = uiState.notificationMessage,
                    color = with(uiState.theme.color.text) {
                        Color(if (uiState.isNotificationError) error else dim)
                    },
                    fontSize = uiState.theme.font.size.normal.sp,
                    fontFamily = Fonts.NotoSans.get(),
                )
                if (uiState.notificationMessageLink.isNotBlank()) {
                    Text(
                        text = uiState.notificationMessageLink,
                        color = Color(uiState.theme.color.text.dim),
                        fontSize = uiState.theme.font.size.normal.sp,
                        fontFamily = Fonts.NotoSans.get(),
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable {
                            browseWord(uriHandler, uiState.notificationMessageLink)
                        },
                    )
                }
            }

            // Game board
            Column(
                Modifier
                    .weight(1f, fill = false)
                    .widthIn(min = 220.dp, max = 280.dp)
                    .aspectRatio(1f)
                    .border(
                        BorderStroke(2.dp, SolidColor(Color(uiState.theme.color.border.neutral)))
                    )
                    .padding(2.dp)
            ) {
                uiState.board.forEachIndexed { rowIndex, row ->
                    Row(Modifier.weight(1f)) {
                        row.forEachIndexed { columnIndex, cell ->
                            Box(
                                Modifier
                                    .weight(1f)
                                    .background(Color(uiState.theme.color.background.clickable))
                                    .clickable {
                                        viewModel.onCellClick(rowIndex, columnIndex)
                                    }
                            ) {
                                Text(
                                    text = cell.text,
                                    color = with(uiState.theme.color.text) {
                                        Color(if (cell.isHighlighted) accent else dim)
                                    },
                                    fontSize = uiState.theme.font.size.cell.sp,
                                    fontFamily = Fonts.NotoSans.get(),
                                    modifier = Modifier.fillMaxSize().border(
                                        BorderStroke(
                                            2.dp,
                                            SolidColor(
                                                with(uiState.theme.color.border) {
                                                    Color(if (cell.isSelected) selected else neutral)
                                                }
                                            )
                                        )
                                    ).wrapContentSize(align = Alignment.Center)
                                        .padding(bottom = 2.dp),
                                )
                                if (cell.hasArrowOut) {
                                    val angle = when {
                                        cell.hasArrowOutLeft -> -90f
                                        cell.hasArrowOutDown -> 180f
                                        cell.hasArrowOutRight -> 90f
                                        else -> 0f // up
                                    }
                                    val alignment = when {
                                        cell.hasArrowOutLeft -> Alignment.CenterStart
                                        cell.hasArrowOutDown -> Alignment.BottomCenter
                                        cell.hasArrowOutRight -> Alignment.CenterEnd
                                        else -> Alignment.TopCenter // up
                                    }
                                    Canvas(
                                        modifier = Modifier.size(10.dp).rotate(angle)
                                            .align(alignment)
                                    ) {
                                        val rect = Rect(Offset.Zero, size)
                                        val arrowUpTailPath = Path().apply {
                                            rect.topLeft.run { moveTo(x, y) }
                                            rect.bottomLeft.run { lineTo(x, y) }
                                            rect.center.run { lineTo(x, y) }
                                            rect.bottomRight.run { lineTo(x, y) }
                                            rect.topRight.run { lineTo(x, y) }
                                        }

                                        drawPath(
                                            color = Color(uiState.theme.color.background.clickable),
                                            path = arrowUpTailPath,
                                            style = Stroke(width = 8.dp.toPx())
                                        )
                                        drawIntoCanvas { canvas ->
                                            canvas.drawOutline(
                                                outline = Outline.Generic(arrowUpTailPath.apply {
                                                    close()
                                                }),
                                                paint = Paint().apply {
                                                    color =
                                                        Color(uiState.theme.color.border.neutral)
                                                }
                                            )
                                        }
                                    }
                                }
                                if (cell.hasArrowIn) {
                                    val angle = when {
                                        cell.hasArrowInLeft -> 90f
                                        cell.hasArrowInDown -> 0f
                                        cell.hasArrowInRight -> -90f
                                        else -> 180f // up
                                    }
                                    val alignment = when {
                                        cell.hasArrowInLeft -> Alignment.CenterStart
                                        cell.hasArrowInDown -> Alignment.BottomCenter
                                        cell.hasArrowInRight -> Alignment.CenterEnd
                                        else -> Alignment.TopCenter // up
                                    }
                                    Canvas(
                                        modifier = Modifier.size(10.dp).rotate(angle)
                                            .align(alignment)
                                    ) {
                                        val rect = Rect(Offset.Zero, size)
                                        val arrowUpHeadPath = Path().apply {
                                            rect.bottomRight.run { moveTo(x, y) }
                                            rect.centerRight.run { lineTo(x, y) }
                                            rect.topCenter.run { lineTo(x, y) }
                                            rect.centerLeft.run { lineTo(x, y) }
                                            rect.bottomLeft.run { lineTo(x, y) }
                                        }

                                        drawPath(
                                            color = Color(uiState.theme.color.background.clickable),
                                            path = arrowUpHeadPath,
                                            style = Stroke(width = 8.dp.toPx())
                                        )
                                        drawIntoCanvas { canvas ->
                                            canvas.drawOutline(
                                                outline = Outline.Generic(arrowUpHeadPath.apply {
                                                    close()
                                                }),
                                                paint = Paint().apply {
                                                    color =
                                                        Color(uiState.theme.color.border.neutral)
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Status message
            Box(
                Modifier.fillMaxWidth().padding(top = 3.dp, bottom = 2.dp)
            ) {
                if (uiState.isPlayer1iconVisible) {
                    Image(
                        bitmap = getImageBitmapFromResources(
                            res = if (uiState.isPlayer1iconComputer) "computer" else "player"
                        ),
                        contentDescription = "",
                        modifier = Modifier.size(48.dp).alpha(0.7f)
                    )
                }
                if (uiState.isPlayer2iconVisible) {
                    Image(
                        bitmap = getImageBitmapFromResources(
                            res = if (uiState.isPlayer2iconComputer) "computer" else "player"
                        ),
                        contentDescription = "",
                        modifier = Modifier.size(48.dp).alpha(0.7f)
                            .align(Alignment.TopEnd).graphicsLayer(scaleX = -1f) // flipped
                    )
                }
                Text(
                    text = uiState.statusMessage,
                    color = Color(uiState.theme.color.text.dim),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    fontFamily = Fonts.NotoSans.get(),
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }

        // Keyboard
        Box {
            Column(Modifier.alpha(if (uiState.isGameOverPopupMenuVisible) 0f else 1f)) {
                Box {
                    Row {
                        "QWERTYUIOP".forEach { letter ->
                            Box(
                                Modifier
                                    .weight(1f).height(48.dp).padding(2.dp)
                                    .border(
                                        BorderStroke(
                                            1.dp,
                                            with(uiState.theme.color.border) {
                                                Color(
                                                    if (uiState.isKeyboardInactive) unselected else neutral
                                                )
                                            },
                                        )
                                    )
                                    .background(Color(uiState.theme.color.background.clickable))
                                    .clickable { viewModel.onLetterKey(letter) }
                            ) {
                                Text(
                                    text = letter.toString(),
                                    color = with(uiState.theme.color.text) {
                                        Color(if (uiState.isKeyboardInactive) inactive else normal)
                                    },
                                    fontSize = 16.sp,
                                    fontFamily = Fonts.NotoSans.get(),
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    }
                }
                Box {
                    Row {
                        Spacer(Modifier.weight(0.5f))
                        "ASDFGHJKL".forEach { letter ->
                            Box(
                                Modifier
                                    .weight(1f).height(48.dp).padding(2.dp)
                                    .border(
                                        BorderStroke(
                                            1.dp,
                                            with(uiState.theme.color.border) {
                                                Color(
                                                    if (uiState.isKeyboardInactive) unselected else neutral
                                                )
                                            },
                                        )
                                    )
                                    .background(Color(uiState.theme.color.background.clickable))
                                    .clickable { viewModel.onLetterKey(letter) }
                            ) {
                                Text(
                                    text = letter.toString(),
                                    color = with(uiState.theme.color.text) {
                                        Color(if (uiState.isKeyboardInactive) inactive else normal)
                                    },
                                    fontSize = 16.sp,
                                    fontFamily = Fonts.NotoSans.get(),
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                        Spacer(Modifier.weight(0.5f))
                    }
                }
                Box(Modifier.fillMaxWidth()) {
                    Row(Modifier.fillMaxWidth()) {
                        Box(
                            Modifier
                                .weight(1.5f).height(48.dp).padding(2.dp)
                                .border(
                                    BorderStroke(
                                        1.dp,
                                        with(uiState.theme.color.border) {
                                            Color(
                                                if (uiState.isKeyboardInactive) unselected else neutral
                                            )
                                        },
                                    )
                                )
                                .background(Color(uiState.theme.color.background.clickable))
                                .clickable { viewModel.onDeleteKey() }
                        ) {
                            Text(
                                text = "Del",
                                color = with(uiState.theme.color.text) {
                                    Color(if (uiState.isKeyboardInactive) inactive else normal)
                                },
                                fontSize = 16.sp,
                                fontFamily = Fonts.NotoSans.get(),
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        "ZXCVBNM".forEach { letter ->
                            Box(
                                Modifier
                                    .weight(1f).height(48.dp).padding(2.dp)
                                    .border(
                                        BorderStroke(
                                            1.dp,
                                            with(uiState.theme.color.border) {
                                                Color(
                                                    if (uiState.isKeyboardInactive) unselected else neutral
                                                )
                                            },
                                        )
                                    )
                                    .background(Color(uiState.theme.color.background.clickable))
                                    .clickable { viewModel.onLetterKey(letter) }
                            ) {
                                Text(
                                    text = letter.toString(),
                                    color = with(uiState.theme.color.text) {
                                        Color(if (uiState.isKeyboardInactive) inactive else normal)
                                    },
                                    fontSize = 16.sp,
                                    fontFamily = Fonts.NotoSans.get(),
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                        Box(
                            Modifier
                                .weight(1.5f).height(48.dp).padding(2.dp)
                                .border(
                                    BorderStroke(
                                        1.dp,
                                        with(uiState.theme.color.border) {
                                            Color(
                                                if (uiState.isKeyboardInactive) unselected else neutral
                                            )
                                        },
                                    )
                                )
                                .background(Color(uiState.theme.color.background.clickable))
                                .clickable { viewModel.onEnterKey() }
                        ) {
                            Text(
                                text = "OK",
                                color = with(uiState.theme.color.text) {
                                    Color(if (uiState.isKeyboardInactive) inactive else normal)
                                },
                                fontSize = 16.sp,
                                fontFamily = Fonts.NotoSans.get(),
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            }
            if (uiState.isGameOverPopupMenuVisible) {
                Column(
                    Modifier.align(Alignment.BottomCenter)
                ) {
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
                    Button(
                        onClick = { viewModel.onPause() },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(uiState.theme.color.background.clickable),
                        ),
                        modifier = Modifier,
                    ) {
                        Text(
                            text = "Options",
                            color = Color(uiState.theme.color.text.normal),
                            fontSize = uiState.theme.font.size.button.sp,
                            textAlign = TextAlign.Center,
                            fontFamily = Fonts.NotoSans.get(),
                            modifier = Modifier.width(200.dp),
                        )
                    }
                }
            }
        }
    }
}

private fun browseWord(uriHandler: UriHandler, word: String) {
    if (word.isBlank()) return
    browseLink(
        uriHandler,
        "https://en.wiktionary.org/wiki/$word#Noun"
    )
}