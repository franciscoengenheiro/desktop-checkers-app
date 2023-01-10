package checkers.ui.compose.windows.main.board

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.*
import checkers.model.board.Board
import checkers.model.board.BoardRun
import checkers.model.board.get
import checkers.model.board.util.getAvalaibleMoves
import checkers.model.moves.move.Player
import checkers.model.moves.move.Square

/**
 * Compose implementation of the game board.
 * @param board Type of the [Board] to represent.
 * @param invert Indicates whether to invert the board.
 * @param localPlayer Player assigned to the game.
 * @param onPlay Callback function to be executed when a play on the board is made.
 * @param enableTargets Indicates whether to show an indication of where a play can be made
 * from a square.
 */
@Composable
fun BoardView(
    board: Board?,
    invert: Boolean,
    localPlayer: Player?,
    onPlay: (fromSqr: Square, toSqr: Square) -> Unit,
    enableTargets: Boolean
) {
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
                    // Retrieve the current selected cell
                    val selCell = selectedCell
                    // A square can only be selected if it does not have a checker
                    // and the current square is the selected Cell
                    val selected = checker != null && selCell == currSquare
                    var showTarget = false
                    if (enableTargets && board is BoardRun && selCell != null)
                        // Only show a target if the option is avalaible and
                        // current square is a valid square to move to
                        showTarget = currSquare in board.getAvalaibleMoves(selCell)
                    CellView(
                        sqr = currSquare,
                        checker = checker,
                        invertRowId = invert,
                        selectedCell = selected,
                        drawTarget = showTarget
                    ) {
                        if (localPlayerTurn(board, localPlayer)) {
                            selectedCell = onCellClick(
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
}