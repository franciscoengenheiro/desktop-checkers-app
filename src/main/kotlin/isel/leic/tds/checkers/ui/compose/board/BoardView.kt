package isel.leic.tds.checkers.ui.compose.board

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.*
import isel.leic.tds.checkers.model.Game
import isel.leic.tds.checkers.model.board.BOARD_DIM
import isel.leic.tds.checkers.model.board.BoardRun
import isel.leic.tds.checkers.model.board.get
import isel.leic.tds.checkers.model.board.utils.getAvalaibleMoves
import isel.leic.tds.checkers.model.moves.move.Square
import isel.leic.tds.checkers.model.moves.square.indexToColumn
import isel.leic.tds.checkers.model.moves.square.indexToRow

@Composable
fun BoardView(
    game: Game?,
    onPlay: (fromSqr: Square, toSqr: Square) -> Unit,
    enableTargets: Boolean
) {
    // println("BoardView Recomposed")
    var selectedCell by remember { mutableStateOf<Square?>(null) }
    var showTarget by remember { mutableStateOf(false) }
    Column {
        repeat(BOARD_DIM) { rowIndex ->
            Row {
                repeat(BOARD_DIM) { colIndex ->
                    // Retrieve square
                    val currSquare = Square(rowIndex.indexToRow(), colIndex.indexToColumn())
                    // Retrieve checker from square if it exists
                    val checker = game?.board?.get(currSquare)
                    // A square can only be selected if it does not have a checker
                    val selected: Boolean = checker != null && selectedCell == currSquare
                    val selCell = selectedCell
                    val board = game?.board
                    if (board is BoardRun && selCell != null)
                        showTarget = currSquare in board.getAvalaibleMoves(selCell)
                    CellView(
                        sqr = currSquare,
                        checker = checker,
                        selectedCell = selected,
                        drawTarget = showTarget && enableTargets
                    ) {
                        if (localPlayerTurn(game)) {
                            if (selCell != null && selCell != currSquare) {
                                try {
                                    onPlay(selCell, currSquare)
                                    selectedCell = null
                                } catch (ex: Exception) {
                                    // TODO ("remove this")
                                }
                            }
                            selectedCell = if (selCell != currSquare
                                && checker?.player == game?.localPlayer) currSquare else null
                        }
                    }
                }
            }
        }
    }
}

private fun localPlayerTurn(game: Game?): Boolean {
    val board = game?.board ?: return false
    return board is BoardRun && board.turn == game.localPlayer
}