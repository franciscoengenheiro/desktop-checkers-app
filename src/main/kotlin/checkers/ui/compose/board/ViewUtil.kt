package checkers.ui.compose.board

import androidx.compose.ui.graphics.Color
import checkers.model.board.BOARD_DIM
import checkers.model.board.Board
import checkers.model.board.BoardRun
import checkers.model.moves.move.Checker
import checkers.model.moves.move.Player
import checkers.model.moves.move.Square
import checkers.ui.compose.base.BaseColors

object CheckerColors {
    var white: Color = BaseColors.LightBrown
        private set
    var black: Color = BaseColors.Brown
        private set
}

fun onCellClick(
    board: Board?,
    localPlayer: Player?,
    selCell: Square?,
    cell: Square,
    onPlay: (fromSqr: Square, toSqr: Square) -> Unit,
    checker: Checker?
): Square? {
    var selectedCell: Square? = selCell
    if (localPlayerTurn(board, localPlayer)) {
        if (selCell != null && selCell != cell)
            try {
                onPlay(selCell, cell)
            } catch(e: Exception) {
                print("OnCellClick: ${e.message}")
            }
        selectedCell = if (selCell != cell && checker?.player === localPlayer) cell
                       else null
    }
    return selectedCell
}

private fun localPlayerTurn(board: Board?, localPlayer: Player?): Boolean {
    if (localPlayer == null) return false
    val b = board ?: return false
    return b is BoardRun && b.turn == localPlayer
}

fun calculateRanges(invert: Boolean): Pair<IntRange, IntProgression> {
    val colRange = 0 until BOARD_DIM
    val rowRange = if (invert) colRange.reversed() else colRange
    return colRange to rowRange
}