package com.alexvt.wordgame.usecases

import com.alexvt.wordgame.AppScope
import com.alexvt.wordgame.model.*
import com.alexvt.wordgame.repository.GameStateRepository
import kotlinx.coroutines.flow.*
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class ConsumeAutoPlayInputsUseCase(
    private val cellClickUseCase: CellClickUseCase,
    private val enterPressUseCase: EnterPressUseCase,
    private val letterInputUseCase: LetterInputUseCase,
    private val gameStateRepository: GameStateRepository,
) {

    // Pending auto inputs are executed after collection from the flow.
    suspend fun execute(beforeComputerMove: suspend () -> Unit): Flow<AutoPlayInput> {
        return flow {
            gameStateRepository.getGameStateFlow().mapNotNull { gameState ->
                gameState.queuedAutoPlayInputs.firstOrNull()
            }.onEach { autoPlayInput ->
                emit(autoPlayInput)
                // at this point the game may have proceeded further
                val upToDateGameState = gameStateRepository.getGameStateFlow().value
                upToDateGameState.queuedAutoPlayInputs.firstOrNull()?.let { upToDateAutoPlayInput ->
                    upToDateGameState.removeFirstAutoPlayInput()
                    when (upToDateAutoPlayInput) {
                        is AutoPlayLetter -> letterInputUseCase.execute(
                            isManualInput = false, upToDateAutoPlayInput.letter
                        )
                        is AutoPlayEnter -> enterPressUseCase.execute(
                            isManualInput = false, beforeComputerMove,
                        )
                        is AutoPlaySelect -> cellClickUseCase.execute(
                            isManualInput = false,
                            upToDateAutoPlayInput.row,
                            upToDateAutoPlayInput.column
                        )
                    }
                }
            }.collect()
        }
    }

    private fun GameState.removeFirstAutoPlayInput() {
        gameStateRepository.updateGameState(
            copy(queuedAutoPlayInputs = queuedAutoPlayInputs.drop(1))
        )
    }

}
