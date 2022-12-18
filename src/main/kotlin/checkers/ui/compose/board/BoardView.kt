package checkers.ui.compose.board

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.*
import checkers.model.board.Board
import checkers.model.board.BoardRun
import checkers.model.board.get
import checkers.model.board.util.getAvalaibleMoves
import checkers.model.moves.move.Player
import checkers.model.moves.move.Square

@Composable
fun BoardView(
    board: Board?,
    invert: Boolean,
    localPlayer: Player?,
    onPlay: (fromSqr: Square, toSqr: Square) -> Unit,
    enableTargets: Boolean
) {
    println("BoardView Recomposed")
    var selectedCell by remember { mutableStateOf<Square?>(null) }
    val (colRange, rowRange) = calculateRanges(invert)
    Column {
        for (rowIndex in rowRange) {
            Row {
                for (colIndex in colRange) {
                    // Retrieve square
                    val currSquare = Square(rowIndex, colIndex)
                    // Retrieve checker from square if it exists
                    val checker = board?.get(currSquare)
                    // A square can only be selected if it does not have a checker
                    val selCell = selectedCell
                    val selected: Boolean = checker != null && selCell == currSquare
                    var showTarget = false
                    if (board is BoardRun && selCell != null && enableTargets)
                        showTarget = currSquare in board.getAvalaibleMoves(selCell)
                    CellView(
                        sqr = currSquare,
                        checker = checker,
                        invertRowId = invert,
                        selectedCell = selected,
                        drawTarget = showTarget
                    ) {
                        // Update selected cell on each cell click
                        selectedCell = onCellClick(
                            board = board,
                            localPlayer = localPlayer,
                            selCell = selCell,
                            cell = currSquare,
                            onPlay = onPlay,
                            checker = checker
                        )
                    }
                }
            }
        }
    }
}