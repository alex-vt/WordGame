package com.alexvt.wordgame.usecases

import com.alexvt.wordgame.AppScope
import com.alexvt.wordgame.model.Direction
import com.alexvt.wordgame.model.Error
import com.alexvt.wordgame.model.GameState
import com.alexvt.wordgame.model.TurnStage
import com.alexvt.wordgame.repository.GameStateRepository
import me.tatarka.inject.annotations.Inject
import kotlin.math.abs

@AppScope
@Inject
class CellClickUseCase(
    private val isCurrentPlayerComputerUseCase: IsCurrentPlayerComputerUseCase,
    private val gameStateRepository: GameStateRepository
) {

    fun execute(isManualInput: Boolean, targetRow: Int, targetColumn: Int) {
        if (isManualInput && isCurrentPlayerComputerUseCase.execute()) return
        val currentGameState = gameStateRepository.getGameStateFlow().value
        with(currentGameState) {
            when (turnStage) {
                TurnStage.PLACING_NEW_LETTER -> {
                    val isCellEmpty = !board.isCellLetter(targetRow, targetColumn)
                    val isCellNextToLetter = board.isCellNextToLetter(targetRow, targetColumn)

                    val isCellSelectableWhenPlacingNewLetter =
                        isCellEmpty && isCellNextToLetter

                    if (isCellSelectableWhenPlacingNewLetter) {
                        unMarkAllAndSelectSingleCell(targetRow, targetColumn)
                    }
                }
                TurnStage.SELECTING_WORD -> {
                    val isLetter = board.isCellLetter(targetRow, targetColumn)
                    val isNoLetterSelected = !board.isAnyLetterSelected()
                    val isNextToSelected = board.isCellNextToSelected(targetRow, targetColumn)
                    val isCellSelected = board.isCellSelected(targetRow, targetColumn)

                    val isCellSelectableWhenSelectingWord =
                        isLetter && !isCellSelected && (isNoLetterSelected || isNextToSelected)

                    if (isCellSelectableWhenSelectingWord) {
                        selectCellAsNext(targetRow, targetColumn)
                    } else if (isCellSelected) {
                        unselectCellAndAllNext(targetRow, targetColumn)
                    }
                }
                else -> { /* game over */
                }
            }
        }
    }

    private fun GameState.selectCellAsNext(targetRow: Int, targetColumn: Int) {
        var boardWithSelections = board
        // first find the latest adjacent cell pointing away, in case direction of selection changed
        run {
            var (selectedRow, selectedColumn) = when {
                board.isCellSelected(targetRow - 1, targetColumn) -> targetRow - 1 to targetColumn
                board.isCellSelected(targetRow + 1, targetColumn) -> targetRow + 1 to targetColumn
                board.isCellSelected(targetRow, targetColumn - 1) -> targetRow to targetColumn - 1
                board.isCellSelected(targetRow, targetColumn + 1) -> targetRow to targetColumn + 1
                else -> return@run
            }
            var (latestAdjacentSelectedRow, latestAdjacentSelectedColumn) =
                selectedRow to selectedColumn
            while (true) {
                board.getNextSelectedCellRowAndColumnOrNull(selectedRow, selectedColumn)
                    ?.let { (nextRow, nextColumn) ->
                        if (areAdjacent(nextRow, nextColumn, targetRow, targetColumn)) {
                            latestAdjacentSelectedRow = nextRow
                            latestAdjacentSelectedColumn = nextColumn
                        }
                        selectedRow = nextRow
                        selectedColumn = nextColumn
                    } ?: break
            }
            // that cell's next cells must be unselected because we changed direction of selection
            board.getNextSelectedCellRowAndColumnOrNull(
                latestAdjacentSelectedRow, latestAdjacentSelectedColumn
            )?.let { (nextRow, nextColumn) ->
                boardWithSelections =
                    boardWithSelections.withCellAndAllNextUnselected(nextRow, nextColumn)
            }
            // and that cell's direction is now to nowhere
            boardWithSelections =
                boardWithSelections.withSelectedLetterDirectionUpdated(
                    targetRow = latestAdjacentSelectedRow,
                    targetColumn = latestAdjacentSelectedColumn,
                    newDirectionTo = Direction.NONE,
                )
        }
        // finally we can select the target cell directly
        boardWithSelections = boardWithSelections
            .withCellSelected(targetRow, targetColumn)
            .withSelectedNeighborDirectionToUpdated(targetRow, targetColumn)
            .withDirectionFromPointingNeighbor(targetRow, targetColumn)
        gameStateRepository.updateGameState(
            copy(
                board = boardWithSelections,
                error = Error.NONE,
            )
        )
    }

    private fun areAdjacent(row1: Int, column1: Int, row2: Int, column2: Int): Boolean =
        abs(row2 - row1) + abs(column2 - column1) == 1

    private fun GameState.unselectCellAndAllNext(targetRow: Int, targetColumn: Int) {
        gameStateRepository.updateGameState(
            copy(
                board = board.withCellAndAllNextUnselected(targetRow, targetColumn),
                error = Error.NONE,
            )
        )
    }

    private fun GameState.unMarkAllAndSelectSingleCell(targetRow: Int, targetColumn: Int) {
        gameStateRepository.updateGameState(
            copy(
                board = board
                    .withNoSelections()
                    .withNoDirections()
                    .withCellSelected(targetRow, targetColumn)
                    .withNewLetterUnMarked(), // previous turn viewing is over when selection cell
                error = Error.NONE,
            )
        )
    }

}