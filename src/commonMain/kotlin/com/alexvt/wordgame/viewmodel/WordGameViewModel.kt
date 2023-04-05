package com.alexvt.wordgame.viewmodel

import com.alexvt.wordgame.AppScope
import com.alexvt.wordgame.model.*
import com.alexvt.wordgame.usecases.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import me.tatarka.inject.annotations.Inject
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

@AppScope
@Inject
class WordGameViewModelUseCases(
    val cellClickUseCase: CellClickUseCase,
    val deletePressUseCase: DeletePressUseCase,
    val enterPressUseCase: EnterPressUseCase,
    val letterInputUseCase: LetterInputUseCase,
    val isCurrentPlayerComputerUseCase: IsCurrentPlayerComputerUseCase,
    val consumeAutoPlayInputsUseCase: ConsumeAutoPlayInputsUseCase,
    val watchVisualGameStateUseCase: WatchVisualGameStateUseCase,
    val pauseGameUseCase: PauseGameUseCase,
    val newGameUseCase: NewGameUseCase,
    val queueAutoPlayInputsUseCase: QueueAutoPlayInputsUseCase,
    val watchNavigationalStateUseCase: WatchNavigationalStateUseCase,
)

@OptIn(ExperimentalCoroutinesApi::class)
class WordGameViewModel(
    private val useCases: WordGameViewModelUseCases,
    private val backgroundCoroutineDispatcher: CoroutineDispatcher,
) : ViewModel() {
    private val backgroundCoroutineScope = viewModelScope + backgroundCoroutineDispatcher

    data class BoardCell(
        val text: String = " ",
        val isSelected: Boolean = false,
        val hasArrowIn: Boolean = false,
        val hasArrowInUp: Boolean = false,
        val hasArrowInDown: Boolean = false,
        val hasArrowInLeft: Boolean = false,
        val hasArrowInRight: Boolean = false,
        val hasArrowOut: Boolean = false,
        val hasArrowOutUp: Boolean = false,
        val hasArrowOutDown: Boolean = false,
        val hasArrowOutLeft: Boolean = false,
        val hasArrowOutRight: Boolean = false,
        val isHighlighted: Boolean = false,
    )

    data class UiState(
        val board: List<List<BoardCell>>,
        val player1name: String,
        val player2name: String,
        val player1words: List<String>,
        val player2words: List<String>,
        val player1score: String,
        val player2score: String,
        val isPlayer1iconVisible: Boolean,
        val isPlayer2iconVisible: Boolean,
        val isPlayer1iconComputer: Boolean,
        val isPlayer2iconComputer: Boolean,
        val notificationMessage: String,
        val isNotificationError: Boolean,
        val statusMessage: String,
        val isKeyboardInactive: Boolean,
        val isGameOverPopupMenuVisible: Boolean,
        val theme: ThemeRecord,
    )

    fun getUiStateFlow(): StateFlow<UiState> =
        useCases.watchVisualGameStateUseCase.execute(viewModelScope).map { it.toUiState() }.stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            initialValue = useCases.watchVisualGameStateUseCase.execute(viewModelScope)
                .value.toUiState()
        )

    fun onCellClick(row: Int, column: Int) {
        useCases.cellClickUseCase.execute(isManualInput = true, row, column)
    }

    fun onLetterKey(letter: Char) {
        useCases.letterInputUseCase.execute(isManualInput = true, letter.lowercaseChar())
    }

    fun onDeleteKey() {
        useCases.deletePressUseCase.execute(isManualInput = true)
    }

    fun onEnterKey() {
        backgroundCoroutineScope.launch {
            useCases.enterPressUseCase.execute(isManualInput = true, beforeComputerMove = {
                // on single threaded platforms, move UI will update before computing moves
                delay(200)
            })
        }
    }

    fun onPause() {
        useCases.pauseGameUseCase.execute()
    }

    fun onNewGame() {
        backgroundCoroutineScope.launch {
            useCases.newGameUseCase.execute(beforeComputerMove = {
                delay(200)
            })
        }
    }

    init {
        backgroundCoroutineScope.launch {
            // if preconfigured as computer vs computer, needs to start without user input
            useCases.queueAutoPlayInputsUseCase.execute(beforeComputerMove = {
                delay(200)
            })
        }
        backgroundCoroutineScope.launch {
            // autoplay should abort on pause and relaunch on resume to rewatch the interrupted move
            useCases.watchNavigationalStateUseCase.execute().flatMapLatest { navigationState ->
                if (!navigationState.isPaused && navigationState.isShown) {
                    useCases.consumeAutoPlayInputsUseCase.execute(beforeComputerMove = {
                        delay(200)
                    }).onEach { // before auto input, todo animate
                        delay(800)
                    }
                } else {
                    emptyFlow()
                }
            }.collect()
        }
    }

    private fun WatchVisualGameStateUseCase.VisualGameState.toUiState(): UiState {
        return UiState(
            board = gameState.board.cellRows.map { cellRow ->
                cellRow.map { cell ->
                    when (cell) {
                        is LetterCell -> {
                            BoardCell(
                                text = cell.letter.toString().uppercase(),
                                isSelected = cell.isSelected,
                                isHighlighted = cell.isNew,
                                hasArrowIn = cell.directionFromPrevious != Direction.NONE,
                                hasArrowInUp = cell.directionFromPrevious == Direction.UP,
                                hasArrowInDown = cell.directionFromPrevious == Direction.DOWN,
                                hasArrowInLeft = cell.directionFromPrevious == Direction.LEFT,
                                hasArrowInRight = cell.directionFromPrevious == Direction.RIGHT,
                                hasArrowOut = cell.directionToNext != Direction.NONE,
                                hasArrowOutUp = cell.directionToNext == Direction.UP,
                                hasArrowOutDown = cell.directionToNext == Direction.DOWN,
                                hasArrowOutLeft = cell.directionToNext == Direction.LEFT,
                                hasArrowOutRight = cell.directionToNext == Direction.RIGHT,
                            )
                        }
                        is EmptyCell -> {
                            BoardCell(
                                isSelected = cell.isSelected,
                            )
                        }
                    }
                }
            },
            player1name = getPlayer1name(gameState.player1, gameState.player2),
            player2name = getPlayer2name(gameState.player1, gameState.player2),
            player1words = gameState.player1.playedWords.toPaddedLines(gameState.board),
            player2words = gameState.player2.playedWords.toPaddedLines(gameState.board),
            player1score = "${gameState.player1.playedWords.getScore()}",
            player2score = "${gameState.player2.playedWords.getScore()}",
            isPlayer1iconVisible = getPlayerIconVisibilities(
                gameState.player1, gameState.player2, gameState.playerTurn, gameState.turnStage
            ).first,
            isPlayer2iconVisible = getPlayerIconVisibilities(
                gameState.player1, gameState.player2, gameState.playerTurn, gameState.turnStage
            ).second,
            isPlayer1iconComputer = gameState.player1.isComputer,
            isPlayer2iconComputer = gameState.player2.isComputer,
            notificationMessage = getNotificationMessage(
                gameState.board,
                gameState.error,
                player1name = getPlayer1name(gameState.player1, gameState.player2),
                player2name = getPlayer2name(gameState.player1, gameState.player2),
                gameState.playerTurn,
                gameState.turnStage,
            ),
            statusMessage = getStatusMessage(
                gameState.player1, gameState.player2, gameState.playerTurn, gameState.turnStage
            ),
            isNotificationError = gameState.error != Error.NONE,
            isKeyboardInactive = useCases.isCurrentPlayerComputerUseCase.execute()
                    || gameState.turnStage == TurnStage.GAME_OVER,
            isGameOverPopupMenuVisible = gameState.turnStage == TurnStage.GAME_OVER,
            theme = theme,
        )
    }

    private fun getPlayer1name(player1: Player, player2: Player): String =
        when {
            player1.isComputer && player2.isComputer -> "Computer 1"
            !player1.isComputer && !player2.isComputer -> "Player 1"
            player1.isComputer -> "Computer"
            else -> "Player"
        }

    private fun getPlayer2name(player1: Player, player2: Player): String =
        when {
            player1.isComputer && player2.isComputer -> "Computer 2"
            !player1.isComputer && !player2.isComputer -> "Player 2"
            player2.isComputer -> "Computer"
            else -> "Player"
        }

    private fun List<Word>.toPaddedLines(board: Board): List<String> =
        map { it.getPlainText() }.paddedWithEmptyTo(minLines = board.getMaxWordsPerPlayer())

    private fun Board.getMaxWordsPerPlayer(): Int =
        cellRows.size * (cellRows.first().size - 1) / 2

    private fun List<String>.paddedWithEmptyTo(minLines: Int): List<String> {
        val remainingLines = (minLines - size).coerceAtLeast(0)
        return this + (0 until remainingLines).map { "" }
    }

    private fun Word.getPlainText(): String {
        return letterCells.map { it.letter }.joinToString(separator = "")
    }

    private fun List<Word>.getScore(): Int =
        map { it.getPlainText() }.sumOf { it.length }

    private fun getNotificationMessage(
        board: Board,
        error: Error,
        player1name: String,
        player2name: String,
        playerTurn: PlayerTurn,
        turnStage: TurnStage,
    ): String {
        val wordOnBoard =
            Word(board.getChosenLetterCellsInOrder()).getPlainText()
        val isShowingLastTurn =
            board.hasNewLetter() && board.getSelectedCellCount() == 0
                    && turnStage != TurnStage.SELECTING_WORD
        return when {
            error == Error.NEW_LETTER_NOT_INCLUDED -> {
                "New letter must be in the word"
            }
            error == Error.WORD_ALREADY_PLAYED -> {
                "Already played: $wordOnBoard"
            }
            error == Error.WORD_NOT_ALLOWED -> {
                "Not allowed: $wordOnBoard"
            }
            isShowingLastTurn -> {
                val previousTurnPlayerName =
                    when (playerTurn) {
                        PlayerTurn.PLAYER_2_TURN -> player1name
                        PlayerTurn.GAME_OVER, // player 2 finishes the game
                        PlayerTurn.PLAYER_1_TURN -> player2name
                    }
                "$previousTurnPlayerName word was: $wordOnBoard"
            }
            else -> ""
        }
    }

    private fun getPlayerIconVisibilities(
        player1: Player,
        player2: Player,
        playerTurn: PlayerTurn,
        turnStage: TurnStage,
    ): Pair<Boolean, Boolean> {
        return when (playerTurn) {
            PlayerTurn.PLAYER_1_TURN -> {
                if (turnStage == TurnStage.GAME_OVER) {
                    false to true // player 1 gave up, show player 2 as winner
                } else {
                    true to false
                }
            }
            PlayerTurn.PLAYER_2_TURN -> {
                if (turnStage == TurnStage.GAME_OVER) {
                    true to false // player 2 gave up, show player 1 as winner
                } else {
                    false to true
                }
            }
            PlayerTurn.GAME_OVER -> {
                val player1advantage =
                    player1.playedWords.getScore() - player2.playedWords.getScore()
                when {
                    player1advantage > 0 -> true to false
                    player1advantage < 0 -> false to true
                    else -> true to true // draw
                }
            }
        }
    }

    private fun getStatusMessage(
        player1: Player,
        player2: Player,
        playerTurn: PlayerTurn,
        turnStage: TurnStage
    ): String {
        if (playerTurn == PlayerTurn.GAME_OVER) {
            val player1advantage =
                player1.playedWords.getScore() - player2.playedWords.getScore()
            val gameOutcomeMessage = when {
                player1advantage > 0 -> "${getPlayer1name(player1, player2)} won!"
                player1advantage < 0 -> "${getPlayer2name(player1, player2)} won!"
                else -> "It's a draw!"
            }
            return "Game over.\n$gameOutcomeMessage"
        } else {

            val playerWhoseTurnNow =
                if (playerTurn == PlayerTurn.PLAYER_1_TURN) {
                    getPlayer1name(player1, player2)
                } else {
                    getPlayer2name(player1, player2)
                }
            val isComputerTurn = useCases.isCurrentPlayerComputerUseCase.execute()
            if (isComputerTurn && turnStage == TurnStage.GAME_OVER) { // computer gave up
                val otherPlayer =
                    if (playerTurn == PlayerTurn.PLAYER_2_TURN) {
                        getPlayer1name(player1, player2)
                    } else {
                        getPlayer2name(player1, player2)
                    }
                return "$playerWhoseTurnNow gave up.\n$otherPlayer won!"
            }
            val instructionMessage =
                if (isComputerTurn) {
                    "computing"
                } else when (turnStage) {
                    TurnStage.PLACING_NEW_LETTER -> "place letter for your word"
                    TurnStage.SELECTING_WORD -> "pick word with new letter"
                    else -> ""
                }
            return "$playerWhoseTurnNow turn:\n$instructionMessage"
        }
    }

}
