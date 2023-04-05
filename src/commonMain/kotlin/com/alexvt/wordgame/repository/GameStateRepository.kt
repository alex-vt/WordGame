package com.alexvt.wordgame.repository

import com.alexvt.wordgame.AppScope
import com.alexvt.wordgame.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class GameStateRepository(
    private val nounsRepository: NounsRepository,
    private val settingsRepository: SettingsRepository,
) {


    private fun getInitialGameState(): GameState =
        with (settingsRepository.readSettings()) {
            GameState(
                board = getInitialBoard(
                    wordText = nounsRepository.getRandomNoun(length = 5, selectionPoolSize = 1000)
                ),
                player1 = Player(
                    playedWords = emptyList(),
                    isComputer = isPlayer1computer,
                    computerMaxWordLength,
                    computerMaxVocabularyNormalizedSize,
                ),
                player2 = Player(
                    playedWords = emptyList(),
                    isComputer = isPlayer2computer,
                    computerMaxWordLength,
                    computerMaxVocabularyNormalizedSize,
                ),
                playerTurn = PlayerTurn.PLAYER_1_TURN,
                turnStage = TurnStage.PLACING_NEW_LETTER,
                error = Error.NONE,
                queuedAutoPlayInputs = emptyList(),
            )
        }

    private fun getInitialBoard(wordText: String): Board {
        val boardSize = 5
        require(wordText.trim().length == boardSize) {
            "wordText must be $boardSize characters"
        }
        val emptyRowsAboveOrBelow = (0 until boardSize / 2).map {
            (0 until boardSize).map {
                EmptyCell()
            }
        }
        val initialWordRows = listOf(
            wordText.lowercase().map { letter ->
                LetterCell(letter)
            }
        )
        return Board(cellRows = emptyRowsAboveOrBelow + initialWordRows + emptyRowsAboveOrBelow)
    }

    private val gameStateMutableFlow: MutableStateFlow<GameState> by lazy {
        MutableStateFlow(getInitialGameState())
    }

    fun getGameStateFlow(): StateFlow<GameState> =
        gameStateMutableFlow.asStateFlow()

    fun updateGameState(newGameState: GameState) {
        gameStateMutableFlow.tryEmit(newGameState)
    }

    fun resetGameState() {
        gameStateMutableFlow.tryEmit(getInitialGameState())
    }
}