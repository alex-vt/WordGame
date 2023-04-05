package com.alexvt.wordgame.usecases

import com.alexvt.wordgame.AppScope
import com.alexvt.wordgame.model.PlayerTurn
import com.alexvt.wordgame.repository.GameStateRepository
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class IsCurrentPlayerComputerUseCase(
    private val gameStateRepository: GameStateRepository
) {

    fun execute(): Boolean {
        val currentGameState = gameStateRepository.getGameStateFlow().value
        return with(currentGameState) {
            when {
                playerTurn == PlayerTurn.PLAYER_1_TURN && player1.isComputer -> true
                playerTurn == PlayerTurn.PLAYER_2_TURN && player2.isComputer -> true
                else -> false
            }
        }
    }

}