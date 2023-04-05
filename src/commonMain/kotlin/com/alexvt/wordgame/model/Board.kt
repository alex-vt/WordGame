package com.alexvt.wordgame.model

enum class Direction {
    NONE, UP, DOWN, LEFT, RIGHT
}

sealed class Cell
data class EmptyCell(
    val isSelected: Boolean = false,
) : Cell()

data class LetterCell(
    val letter: Char,
    val isNew: Boolean = false,
    val isSelected: Boolean = false,
    val directionToNext: Direction = Direction.NONE,
    val directionFromPrevious: Direction = Direction.NONE,
) : Cell()

data class Board(val cellRows: List<List<Cell>>) {

    fun isCellLetter(row: Int, column: Int): Boolean {
        if (row < 0) return false
        if (row >= cellRows.size) return false
        if (column < 0) return false
        if (column >= cellRows[row].size) return false
        return cellRows[row][column] is LetterCell
    }

    fun isCellSelected(row: Int, column: Int): Boolean {
        if (row < 0) return false
        if (row >= cellRows.size) return false
        if (column < 0) return false
        if (column >= cellRows[row].size) return false
        return when (val cell = cellRows[row][column]) {
            is LetterCell -> cell.isSelected
            is EmptyCell -> cell.isSelected
        }
    }

    fun getSelectedCellCount(): Int {
        return cellRows.map { row ->
            row.map { cell ->
                when (cell) {
                    is LetterCell -> {
                        if (cell.isSelected) 1 else 0
                    }
                    is EmptyCell -> {
                        if (cell.isSelected) 1 else 0
                    }
                }
            }
        }.flatten().sum()
    }

    fun isCellNextToLetter(row: Int, column: Int): Boolean {
        if (isCellLetter(row - 1, column)) return true
        if (isCellLetter(row + 1, column)) return true
        if (isCellLetter(row, column - 1)) return true
        if (isCellLetter(row, column + 1)) return true
        return false
    }

    fun isCellNextToSelected(row: Int, column: Int): Boolean {
        if (isCellSelected(row - 1, column)) return true
        if (isCellSelected(row + 1, column)) return true
        if (isCellSelected(row, column - 1)) return true
        if (isCellSelected(row, column + 1)) return true
        return false
    }

    fun withNoDirections(): Board {
        return copy(cellRows = cellRows.map { row ->
            row.map { cell ->
                when (cell) {
                    is LetterCell -> cell.copy(
                        directionToNext = Direction.NONE,
                        directionFromPrevious = Direction.NONE,
                    )
                    is EmptyCell -> cell
                }
            }
        })
    }

    fun withNoSelections(): Board {
        return copy(cellRows = cellRows.map { row ->
            row.map { cell ->
                when (cell) {
                    is LetterCell -> cell.copy(isSelected = false)
                    is EmptyCell -> cell.copy(isSelected = false)
                }
            }
        })
    }

    fun withNewLetterCleared(): Board {
        return copy(cellRows = cellRows.map { row ->
            row.map { cell ->
                if (cell is LetterCell && cell.isNew) {
                    EmptyCell()
                } else {
                    cell
                }
            }
        })
    }

    fun withNewLetterInsteadOfSelection(letter: Char): Board {
        return copy(cellRows = cellRows.map { row ->
            row.map { cell ->
                if (cell is EmptyCell && cell.isSelected) {
                    LetterCell(letter.lowercaseChar(), isNew = true)
                } else if (cell is LetterCell && cell.isSelected) {
                    LetterCell(letter.lowercaseChar(), isNew = true)
                } else {
                    cell
                }
            }
        })
    }

    fun withClearedSelection(): Board {
        return copy(cellRows = cellRows.map { row ->
            row.map { cell ->
                if (cell is LetterCell && cell.isSelected) {
                    EmptyCell(isSelected = true)
                } else {
                    cell
                }
            }
        })
    }

    fun withNewLetterUnMarked(): Board {
        return copy(cellRows = cellRows.map { row ->
            row.map { cell ->
                if (cell is LetterCell) {
                    cell.copy(isNew = false)
                } else {
                    cell
                }
            }
        })
    }

    fun hasNewLetter(): Boolean {
        cellRows.forEach { row ->
            row.forEach { cell ->
                if (cell is LetterCell && cell.isNew) {
                    return true
                }
            }
        }
        return false
    }

    fun isAnyLetterSelected(): Boolean {
        cellRows.forEach { row ->
            row.forEach { cell ->
                if (cell is LetterCell && cell.isSelected) {
                    return true
                }
            }
        }
        return false
    }

    fun withCellAndAllNextUnselected(targetRow: Int, targetColumn: Int): Board {
        var rowToUnselect = targetRow
        var columnToUnselect = targetColumn
        var boardWithSelections = this
        while (boardWithSelections.isCellSelected(rowToUnselect, columnToUnselect)) {
            val directionToNext =
                boardWithSelections.getDirectionToNext(rowToUnselect, columnToUnselect)
            boardWithSelections = boardWithSelections
                .withCellUnselected(rowToUnselect, columnToUnselect)
                .withSelectedNeighborDirectionToUpdated(rowToUnselect, columnToUnselect)
            when (directionToNext) {
                Direction.UP -> --rowToUnselect
                Direction.DOWN -> ++rowToUnselect
                Direction.LEFT -> --columnToUnselect
                Direction.RIGHT -> ++columnToUnselect
                else -> {} /* reached the end of selections */
            }
        }
        return boardWithSelections
    }

    fun getNextSelectedCellRowAndColumnOrNull(row: Int, column: Int): Pair<Int, Int>? {
        val cell = cellRows[row][column]
        return if (cell is LetterCell && cell.isSelected) {
            when (cell.directionToNext) {
                Direction.UP -> row - 1 to column
                Direction.DOWN -> row + 1 to column
                Direction.LEFT -> row to column - 1
                Direction.RIGHT -> row to column + 1
                Direction.NONE -> null
            }?.takeIf { (nextRow, nextColumn) ->
                (cellRows[nextRow][nextColumn] as? LetterCell)?.isSelected ?: false
            }
        } else {
            null
        }
    }

    fun getSequenceOfLetters(row: Int, column: Int): List<LetterCell> {
        val cell = cellRows[row][column]
        return if (cell is LetterCell) {
            when (cell.directionToNext) {
                Direction.UP -> listOf(cell) + getSequenceOfLetters(row - 1, column)
                Direction.DOWN -> listOf(cell) + getSequenceOfLetters(row + 1, column)
                Direction.LEFT -> listOf(cell) + getSequenceOfLetters(row, column - 1)
                Direction.RIGHT -> listOf(cell) + getSequenceOfLetters(row, column + 1)
                Direction.NONE -> listOf(cell)
            }
        } else {
            emptyList()
        }
    }

    fun getNextLetterOrNull(row: Int, column: Int): LetterCell? {
        val cell = cellRows[row][column]
        return if (cell is LetterCell && cell.isSelected) {
            when (cell.directionToNext) {
                Direction.UP -> cellRows[row - 1][column]
                Direction.DOWN -> cellRows[row + 1][column]
                Direction.LEFT -> cellRows[row][column - 1]
                Direction.RIGHT -> cellRows[row][column + 1]
                Direction.NONE -> null
            } as? LetterCell
        } else {
            null
        }
    }

    /**
     * Each selected cell will lead to the end of the selected word - this is a word tail.
     * Other cells will produce empty word tails.
     * So, the selected word is the longest word tail.
     */
    fun getChosenLetterCellsInOrder(): List<LetterCell> {
        val wordTails =
            (cellRows.indices).map { rowIndex ->
                (cellRows[rowIndex].indices).map { columnIndex ->
                    getSequenceOfLetters(rowIndex, columnIndex)
                }
            }.flatten()
        return wordTails.maxBy { it.size }
    }

    fun getStartingLetterCellsInOrder(): List<LetterCell> {
        val middleRowIndex = cellRows.size / 2
        return cellRows[middleRowIndex].filterIsInstance<LetterCell>()
    }

    fun hasEmptyCells(): Boolean {
        cellRows.forEach { row ->
            row.forEach { cell ->
                if (cell is EmptyCell) {
                    return true
                }
            }
        }
        return false
    }

    fun isNewLetterSelected(): Boolean {
        cellRows.forEach { row ->
            row.forEach { cell ->
                if (cell is LetterCell && cell.isSelected && cell.isNew) {
                    return true
                }
            }
        }
        return false
    }

    fun withSelectedLetterDirectionUpdated(
        targetRow: Int,
        targetColumn: Int,
        conditionDirectionTo: Direction? = null,
        newDirectionTo: Direction
    ): Board {
        return copy(cellRows = cellRows.mapIndexed { rowIndex, row ->
            row.mapIndexed { columnIndex, cell ->
                if (cell is LetterCell && cell.isSelected
                    && rowIndex == targetRow && columnIndex == targetColumn
                ) {
                    if (conditionDirectionTo == null || cell.directionToNext == conditionDirectionTo) {
                        cell.copy(directionToNext = newDirectionTo)
                    } else {
                        cell
                    }
                } else {
                    cell
                }
            }
        })
    }

    private fun getDirectionToNext(targetRow: Int, targetColumn: Int): Direction =
        when (val cell = cellRows[targetRow][targetColumn]) {
            is LetterCell -> cell.directionToNext
            else -> Direction.NONE
        }

    /**
     * If the target cell is a selected letter, any adjacent selected letter
     * without direction should point to it.
     * If the target cell is an unselected letter or empty, any adjacent selected letter
     * pointing to it should point nowhere instead.
     */
    fun withSelectedNeighborDirectionToUpdated(row: Int, column: Int): Board {
        val cell = cellRows[row][column]
        if (cell is LetterCell && cell.isSelected) {
            return withSelectedLetterDirectionUpdated(
                row - 1, column,
                conditionDirectionTo = Direction.NONE, newDirectionTo = Direction.DOWN,
            ).withSelectedLetterDirectionUpdated(
                row + 1, column,
                conditionDirectionTo = Direction.NONE, newDirectionTo = Direction.UP,
            ).withSelectedLetterDirectionUpdated(
                row, column - 1,
                conditionDirectionTo = Direction.NONE, newDirectionTo = Direction.RIGHT,
            ).withSelectedLetterDirectionUpdated(
                row, column + 1,
                conditionDirectionTo = Direction.NONE, newDirectionTo = Direction.LEFT,
            )
        } else {
            return withSelectedLetterDirectionUpdated(
                row - 1, column,
                conditionDirectionTo = Direction.DOWN, newDirectionTo = Direction.NONE,
            ).withSelectedLetterDirectionUpdated(
                row + 1, column,
                conditionDirectionTo = Direction.UP, newDirectionTo = Direction.NONE,
            ).withSelectedLetterDirectionUpdated(
                row, column - 1,
                conditionDirectionTo = Direction.RIGHT, newDirectionTo = Direction.NONE,
            ).withSelectedLetterDirectionUpdated(
                row, column + 1,
                conditionDirectionTo = Direction.LEFT, newDirectionTo = Direction.NONE,
            )
        }
    }

    fun withDirectionFromPointingNeighbor(
        targetRow: Int,
        targetColumn: Int,
    ): Board {
        require(targetRow in cellRows.indices) {
            "Target row must be within [0, ${cellRows.size})"
        }
        require(targetColumn in cellRows[targetRow].indices) {
            "Target column must be within [0, ${cellRows[targetRow].size})"
        }
        val adjacentCells = listOf(
            targetRow - 1 to targetColumn,
            targetRow + 1 to targetColumn,
            targetRow to targetColumn - 1,
            targetRow to targetColumn + 1,
        )
        adjacentCells.forEach { (adjacentRow, adjacentColumn) ->
            if (adjacentRow in cellRows.indices && adjacentColumn in cellRows[targetRow].indices) {
                val adjacentCell = cellRows[adjacentRow][adjacentColumn]
                val isPointingToTarget =
                    targetRow to targetColumn ==
                            getNextSelectedCellRowAndColumnOrNull(adjacentRow, adjacentColumn)
                if (isPointingToTarget && adjacentCell is LetterCell && adjacentCell.isSelected) {
                    val directionFromPrevious = when (adjacentCell.directionToNext) {
                        Direction.UP -> Direction.DOWN
                        Direction.DOWN -> Direction.UP
                        Direction.LEFT -> Direction.RIGHT
                        Direction.RIGHT -> Direction.LEFT
                        Direction.NONE -> Direction.NONE
                    }
                    return copy(cellRows = cellRows.mapIndexed { rowIndex, row ->
                        row.mapIndexed { columnIndex, cell ->
                            when (cell) {
                                is LetterCell -> {
                                    if (rowIndex == targetRow && columnIndex == targetColumn) {
                                        cell.copy(directionFromPrevious = directionFromPrevious)
                                    } else {
                                        cell
                                    }
                                }
                                is EmptyCell -> {
                                    cell
                                }
                            }
                        }
                    })
                }
            }
        }
        return this
    }

    fun withCellSelected(targetRow: Int, targetColumn: Int): Board {
        require(targetRow in cellRows.indices) {
            "Target row must be within [0, ${cellRows.size})"
        }
        require(targetColumn in cellRows[targetRow].indices) {
            "Target column must be within [0, ${cellRows[targetRow].size})"
        }
        return copy(cellRows = cellRows.mapIndexed { rowIndex, row ->
            row.mapIndexed { columnIndex, cell ->
                when (cell) {
                    is LetterCell -> {
                        if (rowIndex == targetRow && columnIndex == targetColumn) {
                            cell.copy(isSelected = true)
                        } else {
                            cell
                        }
                    }
                    is EmptyCell -> {
                        if (rowIndex == targetRow && columnIndex == targetColumn) {
                            cell.copy(isSelected = true)
                        } else {
                            cell
                        }
                    }
                }
            }
        })
    }

    private fun withCellUnselected(targetRow: Int, targetColumn: Int): Board {
        require(targetRow in cellRows.indices) {
            "Target row must be within [0, ${cellRows.size})"
        }
        require(targetColumn in cellRows[targetRow].indices) {
            "Target column must be within [0, ${cellRows[targetRow].size})"
        }
        return copy(cellRows = cellRows.mapIndexed { rowIndex, row ->
            row.mapIndexed { columnIndex, cell ->
                when (cell) {
                    is LetterCell -> {
                        if (rowIndex == targetRow && columnIndex == targetColumn) {
                            cell.copy(
                                isSelected = false,
                                directionToNext = Direction.NONE,
                                directionFromPrevious = Direction.NONE,
                            )
                        } else {
                            cell
                        }
                    }
                    is EmptyCell -> {
                        if (rowIndex == targetRow && columnIndex == targetColumn) {
                            cell.copy(isSelected = false)
                        } else {
                            cell
                        }
                    }
                }
            }
        })
    }

    fun withSelectionMarkedAsNew(): Board {
        return copy(cellRows = cellRows.map { row ->
            row.map { cell ->
                when (cell) {
                    is LetterCell -> {
                        cell.copy(isNew = cell.isSelected)
                    }
                    is EmptyCell -> {
                        cell
                    }
                }
            }
        })
    }
}
