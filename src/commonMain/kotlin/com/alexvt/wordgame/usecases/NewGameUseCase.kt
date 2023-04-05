package com.alexvt.wordgame.usecases

import com.alexvt.wordgame.AppScope
import com.alexvt.wordgame.repository.GameStateRepository
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class NewGameUseCase(
    private val resumeGameUseCase: ResumeGameUseCase,
    private val gameStateRepository: GameStateRepository,
    private val queueAutoPlayInputsUseCase: QueueAutoPlayInputsUseCase,
) {

    suspend fun execute(beforeComputerMove: suspend () -> Unit) {
        gameStateRepository.resetGameState()
        resumeGameUseCase.execute()
        queueAutoPlayInputsUseCase.execute(beforeComputerMove)
    }

}