package checkers.ui.compose.windows.main.board

import androidx.compose.ui.graphics.Color
import checkers.model.board.BOARD_DIM
import checkers.model.board.Board
import checkers.model.board.BoardRun
import checkers.model.moves.move.Checker
import checkers.model.moves.move.Player
import checkers.model.moves.move.Square
import checkers.ui.compose.base.BaseColors

/**
 * Defines the colors of the squares that compose the board.
 * The colors can only be defined here.
 */
object SquareColors {
    var white: Color = BaseColors.LightBrown
        private set
    var black: Color = BaseColors.Brown
        private set
}

/**
 *
 */
fun onCellClick(
    localPlayer: Player?,
    selCell: Square?,
    cell: Square,
    onPlay: (fromSqr: Square, toSqr: Square) -> Unit,
    checker: Checker?
): Square? {
    // Only allow a play if a cell is selected and the selected cell is not the
    // same cell as the selected cell
    if (selCell != null && selCell != cell) onPlay(selCell, cell)
    return if (selCell != cell && checker?.player === localPlayer) cell
           else null
}

/**
 * Indicates if the [localPlayer] has permission to play on the [board].
 * @param board type of the [Board] to evaluate.
 * @param localPlayer Player assigned to this game.
 */
fun localPlayerTurn(board: Board?, localPlayer: Player?): Boolean {
    if (localPlayer == null) return false
    val b = board ?: return false
    return b is BoardRun && b.turn == localPlayer
}

/**
 * Calculates row and column range based on [BOARD_DIM] value.
 * @param invert indicates if the row range must be inverted.
 * @return A pair with the column and row ranges.
 */
fun calculateRanges(invert: Boolean): Pair<IntRange, IntProgression> {
    val colRange = 0 until BOARD_DIM
    val rowRange = if (invert) colRange.reversed() else colRange
    return colRange to rowRange
}