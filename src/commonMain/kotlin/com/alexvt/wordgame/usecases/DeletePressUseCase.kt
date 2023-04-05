package com.alexvt.wordgame.usecases

import com.alexvt.wordgame.AppScope
import com.alexvt.wordgame.model.Error
import com.alexvt.wordgame.model.GameState
import com.alexvt.wordgame.model.TurnStage
import com.alexvt.wordgame.repository.GameStateRepository
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class DeletePressUseCase(
    private val isCurrentPlayerComputerUseCase: IsCurrentPlayerComputerUseCase,
    private val gameStateRepository: GameStateRepository
) {

    fun execute(isManualInput: Boolean) {
        if (isManualInput && isCurrentPlayerComputerUseCase.execute()) return
        val currentGameState = gameStateRepository.getGameStateFlow().value
        with(currentGameState) {
            when (turnStage) {
                TurnStage.PLACING_NEW_LETTER -> stopShowingPreviousTurn()
                TurnStage.SELECTING_WORD -> unselectAllAndRemoveNewLetter()
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

    private fun GameState.unselectAllAndRemoveNewLetter() {
        gameStateRepository.updateGameState(
            copy(
                board = board.withNoSelections().withNoDirections().withNewLetterCleared(),
                turnStage = TurnStage.PLACING_NEW_LETTER,
                error = Error.NONE,
            )
        )
    }

}