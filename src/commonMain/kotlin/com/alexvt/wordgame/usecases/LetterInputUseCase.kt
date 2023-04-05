package com.alexvt.wordgame.usecases

import com.alexvt.wordgame.AppScope
import com.alexvt.wordgame.model.GameState
import com.alexvt.wordgame.model.TurnStage
import com.alexvt.wordgame.repository.GameStateRepository
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class LetterInputUseCase(
    private val isCurrentPlayerComputerUseCase: IsCurrentPlayerComputerUseCase,
    private val gameStateRepository: GameStateRepository
) {

    fun execute(isManualInput: Boolean, letter: Char) {
        if (isManualInput && isCurrentPlayerComputerUseCase.execute()) return
        val currentGameState = gameStateRepository.getGameStateFlow().value
        with(currentGameState) {
            when (turnStage) {
                TurnStage.PLACING_NEW_LETTER -> {
                    val isOneCellSelected = board.getSelectedCellCount() == 1

                    if (isOneCellSelected) {
                        putLetterInsteadOfSelection(letter)
                    }
                }
                TurnStage.SELECTING_WORD -> {
                    val isOneCellSelected = board.getSelectedCellCount() == 1
                    val isNewLetterSelected = board.isNewLetterSelected()

                    val isOnlyNewLetterSelected =
                        isOneCellSelected && isNewLetterSelected

                    if (isOnlyNewLetterSelected) {
                        putLetterInsteadOfSelection(letter)
                    }
                }
                TurnStage.GAME_OVER -> {} /* no more letters */
            }

        }
    }

    private fun GameState.putLetterInsteadOfSelection(letter: Char) {
        gameStateRepository.updateGameState(
            copy(
                board = board.withNewLetterInsteadOfSelection(letter),
                turnStage = TurnStage.SELECTING_WORD,
            )
        )
    }

}