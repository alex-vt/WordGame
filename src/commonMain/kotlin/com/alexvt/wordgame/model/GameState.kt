package com.alexvt.wordgame.model

data class Word(val letterCells: List<LetterCell>)

data class Player(
    val playedWords: List<Word>,
    val isComputer: Boolean,
    val computerMaxWordLength: Int,
    val computerMaxVocabularyNormalizedSize: Double, // 0.5: more common half of nouns, 1: all nouns
)

enum class PlayerTurn {
    PLAYER_1_TURN, PLAYER_2_TURN, GAME_OVER,
}

enum class TurnStage {
    PLACING_NEW_LETTER, SELECTING_WORD, GAME_OVER,
}

enum class Error {
    NEW_LETTER_NOT_INCLUDED, WORD_ALREADY_PLAYED, WORD_NOT_ALLOWED, NONE,
}

sealed class AutoPlayInput
data class AutoPlayLetter(val letter: Char) : AutoPlayInput()
data class AutoPlaySelect(val row: Int, val column: Int) : AutoPlayInput()
object AutoPlayEnter : AutoPlayInput()

data class GameState(
    val board: Board,
    val player1: Player,
    val player2: Player,
    val playerTurn: PlayerTurn,
    val turnStage: TurnStage,
    val error: Error,
    val queuedAutoPlayInputs: List<AutoPlayInput>,
)
