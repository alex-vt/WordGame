package com.alexvt.wordgame.usecases

import com.alexvt.wordgame.AppScope
import com.alexvt.wordgame.model.*
import com.alexvt.wordgame.repository.GameStateRepository
import com.alexvt.wordgame.repository.NounsRepository
import com.alexvt.wordgame.usecases.internal.CheckIfWordAllowedUseCase
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class QueueAutoPlayInputsUseCase(
    private val isCurrentPlayerComputerUseCase: IsCurrentPlayerComputerUseCase,
    private val checkIfWordAllowedUseCase: CheckIfWordAllowedUseCase,
    private val gameStateRepository: GameStateRepository,
    private val nounsRepository: NounsRepository,
) {

    suspend fun execute(beforeComputerMove: suspend () -> Unit) {
        if (!isCurrentPlayerComputerUseCase.execute()) return
        val currentGameState = gameStateRepository.getGameStateFlow().value
        if (currentGameState.queuedAutoPlayInputs.isNotEmpty()) return

        beforeComputerMove()

        with(currentGameState) {
            // For each letter to place in eligible places on board, we filter the nouns dictionary.
            // Then each letter in all eligible places makes a seed list of possible words
            // consisting from just that letter first, but eventually growing.
            val allPotentialWords = ('a'..'z').flatMap { letter ->
                val allUsedLetters = (board.getAllLetters() + letter).toSet()
                val usedPartOfDictionary =
                    nounsRepository.getMostCommon(
                        usePartOfDictionary = getCurrentPlayer().computerMaxVocabularyNormalizedSize
                    ).filter { dictionaryWord ->
                        dictionaryWord.all { it in allUsedLetters }
                    }
                var letterPotentialWords = board.getSeedWordsForLetter(letter)
                (1 until getCurrentPlayer().computerMaxWordLength).forEach { currentWordLength ->
                    // todo optimize / inject coroutine delays for single threaded platforms
                    val potentialWordsOverCurrentLength = letterPotentialWords
                        .takeLastWhile { it.length == currentWordLength }
                        .flatMap { it.getOneLetterLongerPotentialWords(board) }
                        .filter { it.canBePartOfActualWord(usedPartOfDictionary) }
                    letterPotentialWords = letterPotentialWords + potentialWordsOverCurrentLength
                }
                letterPotentialWords
            }

            val shuffledPotentialWordsWithShortestFirst =
                allPotentialWords.groupBy { it.length }.toList()
                    .sortedBy { (length, _) -> length }
                    .flatMap { (_, wordsOfLength) -> wordsOfLength.shuffled() }

            // finally the possible words are random but sorted by length, so we aim for the last
            shuffledPotentialWordsWithShortestFirst.lastOrNull { potentialWord ->
                checkIfWordAllowedUseCase.execute(
                    word = potentialWord.toString(),
                    usePartOfDictionary = getCurrentPlayer().computerMaxVocabularyNormalizedSize
                ) == Error.NONE
            }?.let { chosenWord ->
                val chosenWordLetters = chosenWord.wordLetters
                // queue: keyboard OK, click cell, input new letter, letter cells, keyboard OK
                setAutoPlayInputs(
                    listOf(
                        AutoPlayEnter,
                        chosenWordLetters.first { it.isNew }
                            .let { AutoPlaySelect(it.row, it.column) },
                        AutoPlayLetter(chosenWordLetters.first { it.isNew }.letter),
                    ) + chosenWordLetters.map {
                        AutoPlaySelect(
                            it.row,
                            it.column
                        )
                    } + AutoPlayEnter
                )
            } ?: giveUp() // no solutions
        }
    }

    private fun Board.getSeedWordsForLetter(letter: Char): List<PotentialWord> {
        return cellRows.flatMapIndexed { rowIndex, rowCells ->
            rowCells.flatMapIndexed { columnIndex, cell ->
                if (cell is EmptyCell && isCellNextToLetter(rowIndex, columnIndex)) {
                    listOf(
                        PotentialWord(
                            listOf(
                                WordLetter(letter, rowIndex, columnIndex, isNew = true)
                            )
                        )
                    )
                } else {
                    emptyList()
                }
            }
        }
    }

    private fun Board.getAllLetters(): List<Char> =
        cellRows.flatMapIndexed { rowIndex, rowCells ->
            rowCells.flatMapIndexed { columnIndex, cell ->
                if (cell is LetterCell) {
                    listOf(cell.letter)
                } else {
                    emptyList()
                }
            }
        }

    private fun GameState.getCurrentPlayer(): Player =
        when (playerTurn) {
            PlayerTurn.PLAYER_1_TURN -> player1
            else -> player2
        }

    private data class WordLetter(
        val letter: Char,
        val row: Int,
        val column: Int,
        val isNew: Boolean
    )

    private data class PotentialWord(val wordLetters: List<WordLetter>) {
        val length = wordLetters.size
        override fun toString(): String {
            return wordLetters.map { it.letter }.joinToString(separator = "")
        }
    }

    private fun PotentialWord.canBePartOfActualWord(usedPartOfDictionary: List<String>): Boolean {
        val potentialWordText = toString()
        return usedPartOfDictionary.any { potentialWordText in it }
    }

    private fun PotentialWord.getOneLetterLongerPotentialWords(board: Board): List<PotentialWord> {
        val (startRow, startColumn) = this.wordLetters.first().run { row to column }
        val (endRow, endColumn) = this.wordLetters.last().run { row to column }
        return listOfNotNull(
            getOneLetterLongerPotentialWordOrNull(board, startRow - 1, startColumn, true),
            getOneLetterLongerPotentialWordOrNull(board, startRow + 1, startColumn, true),
            getOneLetterLongerPotentialWordOrNull(board, startRow, startColumn - 1, true),
            getOneLetterLongerPotentialWordOrNull(board, startRow, startColumn + 1, true),
            getOneLetterLongerPotentialWordOrNull(board, endRow - 1, endColumn, false),
            getOneLetterLongerPotentialWordOrNull(board, endRow + 1, endColumn, false),
            getOneLetterLongerPotentialWordOrNull(board, endRow, endColumn - 1, false),
            getOneLetterLongerPotentialWordOrNull(board, endRow, endColumn + 1, false),
        )
    }

    private fun PotentialWord.getOneLetterLongerPotentialWordOrNull(
        board: Board,
        adjacentCellRow: Int,
        adjacentCellColumn: Int,
        isAtBeginning: Boolean,
    ): PotentialWord? {
        if (adjacentCellRow < 0) return null
        if (adjacentCellColumn < 0) return null
        if (adjacentCellRow >= board.cellRows.size) return null
        if (adjacentCellColumn >= board.cellRows[adjacentCellRow].size) return null
        if (!board.isCellLetter(adjacentCellRow, adjacentCellColumn)) return null
        if (containsCell(adjacentCellRow, adjacentCellColumn)) return null
        val newCharacter =
            (board.cellRows[adjacentCellRow][adjacentCellColumn] as? LetterCell)?.letter
                ?: return null
        val newWordLetter = WordLetter(
            letter = newCharacter,
            isNew = false,
            row = adjacentCellRow,
            column = adjacentCellColumn
        )
        return if (isAtBeginning) {
            listOf(newWordLetter) + wordLetters
        } else {
            wordLetters + newWordLetter
        }.let { PotentialWord(it) }
    }

    private fun PotentialWord.containsCell(row: Int, column: Int): Boolean =
        wordLetters.any { letter -> letter.row == row && letter.column == column }

    private fun GameState.giveUp() {
        gameStateRepository.updateGameState(
            copy(turnStage = TurnStage.GAME_OVER)
        )
    }

    private fun GameState.setAutoPlayInputs(autoPlayInputs: List<AutoPlayInput>) {
        gameStateRepository.updateGameState(
            copy(queuedAutoPlayInputs = autoPlayInputs)
        )
    }

}
