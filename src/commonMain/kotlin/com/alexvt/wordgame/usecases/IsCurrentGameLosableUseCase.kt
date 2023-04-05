package com.alexvt.wordgame.usecases

import com.alexvt.wordgame.AppScope
import com.alexvt.wordgame.model.TurnStage
import com.alexvt.wordgame.repository.GameStateRepository
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class IsCurrentGameLosableUseCase(
    private val gameStateRepository: GameStateRepository
) {

    // Game is losable when not over yet and a player completed any move.
    fun execute(): Boolean {
        return with(gameStateRepository.getGameStateFlow().value) {
            val isGameOver = turnStage == TurnStage.GAME_OVER
            val player1moved = player1.playedWords.isNotEmpty()
            val player2moved = player2.playedWords.isNotEmpty()
            !isGameOver && (player1moved || player2moved)
        }
    }

}