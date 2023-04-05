package com.alexvt.wordgame.usecases.internal

import com.alexvt.wordgame.AppScope
import com.alexvt.wordgame.model.Error
import com.alexvt.wordgame.model.Word
import com.alexvt.wordgame.repository.GameStateRepository
import com.alexvt.wordgame.repository.NounsRepository
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class CheckIfWordAllowedUseCase(
    private val gameStateRepository: GameStateRepository,
    private val nounsRepository: NounsRepository,
) {

    fun execute(word: String, usePartOfDictionary: Double): Error {
        val currentGameState = gameStateRepository.getGameStateFlow().value
        with(currentGameState) {
            val startingWord = Word(board.getStartingLetterCellsInOrder())
            val isWordPlayed =
                (player1.playedWords + player2.playedWords + startingWord).any { playedWord ->
                    playedWord.toText() == word
                }
            if (isWordPlayed) {
                return Error.WORD_ALREADY_PLAYED
            }

            val isWordInDictionary = nounsRepository.isAllowed(word, usePartOfDictionary)
            if (!isWordInDictionary) {
                return Error.WORD_NOT_ALLOWED
            }
        }
        return Error.NONE
    }

    private fun Word.toText(): String =
        letterCells.map { it.letter }.joinToString(separator = "")

}
