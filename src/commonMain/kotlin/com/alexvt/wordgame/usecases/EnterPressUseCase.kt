package com.alexvt.wordgame.usecases

import com.alexvt.wordgame.AppScope
import com.alexvt.wordgame.model.*
import com.alexvt.wordgame.repository.GameStateRepository
import com.alexvt.wordgame.usecases.internal.CheckIfWordAllowedUseCase
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class EnterPressUseCase(
    private val isCurrentPlayerComputerUseCase: IsCurrentPlayerComputerUseCase,
    private val checkIfWordAllowedUseCase: CheckIfWordAllowedUseCase,
    private val queueAutoPlayInputsUseCase: QueueAutoPlayInputsUseCase,
    private val gameStateRepository: GameStateRepository,
) {

    suspend fun execute(isManualInput: Boolean, beforeComputerMove: suspend () -> Unit) {
        if (isManualInput && isCurrentPlayerComputerUseCase.execute()) return
        val currentGameState = gameStateRepository.getGameStateFlow().value
        with(currentGameState) {
            when (turnStage) {
                TurnStage.PLACING_NEW_LETTER -> stopShowingPreviousTurn()
                TurnStage.SELECTING_WORD -> tryFinishTurn(beforeComputerMove)
                TurnStage.GAME_OVER -> stopShowingPreviousTurn()
            }
        }
    }

    private fun GameState.stopShowingPreviousTurn() {
        gameStateRepository.updateGameState(
            copy(
                board = board.withNoSelections().withNoDirections().withNewLetterUnMarked(),
            )
        )
    }

    private fun Word.toText(): String =
        letterCells.map { it.letter }.joinToString(separator = "")

    private suspend fun GameState.tryFinishTurn(beforeComputerMove: suspend () -> Unit) {
        val isSelectingWord = turnStage == TurnStage.SELECTING_WORD
        if (!isSelectingWord) {
            return // too early for the turn
        }

        val areLettersSelected = board.getSelectedCellCount() > 1
        if (!areLettersSelected) {
            return // no meaningful word yet
        }

        val isNewLetterAmongSelected = board.isNewLetterSelected()
        if (!isNewLetterAmongSelected) {
            showError(Error.NEW_LETTER_NOT_INCLUDED)
            return
        }

        val selectedWord = Word(board.getChosenLetterCellsInOrder())
        val usePartOfDictionary =
            if (isCurrentPlayerComputerUseCase.execute()) {
                (if (playerTurn == PlayerTurn.PLAYER_1_TURN) player1 else player2)
                    .computerMaxVocabularyNormalizedSize
            } else {
                1.0 // human players may use all of the dictionary
            }
        val wordCheckResultErrorOrNone =
            checkIfWordAllowedUseCase.execute(selectedWord.toText(), usePartOfDictionary)
        when (wordCheckResultErrorOrNone) {
            Error.NONE -> finishTurn(selectedWord, beforeComputerMove)
            else -> showError(wordCheckResultErrorOrNone)
        }
    }

    private fun GameState.showError(error: Error) {
        gameStateRepository.updateGameState(
            copy(error = error)
        )
    }

    private suspend fun GameState.finishTurn(
        selectedWord: Word,
        beforeComputerMove: suspend () -> Unit
    ) {
        val isPlayer1turn = playerTurn == PlayerTurn.PLAYER_1_TURN
        val isNoEmptyCells = !board.hasEmptyCells()
        val nextPlayerTurn = when {
            isNoEmptyCells -> PlayerTurn.GAME_OVER
            isPlayer1turn -> PlayerTurn.PLAYER_2_TURN
            else -> PlayerTurn.PLAYER_1_TURN
        }
        val nextTurnStage = when {
            isNoEmptyCells -> TurnStage.GAME_OVER
            else -> TurnStage.PLACING_NEW_LETTER
        }

        val player1newWords =
            if (isPlayer1turn) listOf(selectedWord) else emptyList()
        val player2newWords =
            if (isPlayer1turn) emptyList() else listOf(selectedWord)

        gameStateRepository.updateGameState(
            copy(
                board = board.withNoSelections(),
                player1 = player1.copy(playedWords = player1.playedWords + player1newWords),
                player2 = player2.copy(playedWords = player2.playedWords + player2newWords),
                playerTurn = nextPlayerTurn,
                turnStage = nextTurnStage,
                error = Error.NONE,
            )
        )
        queueAutoPlayInputsUseCase.execute(beforeComputerMove)
    }

}