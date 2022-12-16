package isel.leic.tds.checkers.model.board.utils

import isel.leic.tds.checkers.model.board.BoardRun
import isel.leic.tds.checkers.model.board.MAX_MOVES_WITHOUT_CAPTURE

/**
 * Ensure the other player has at least one valid move to perform in the next turn.
 */
internal fun BoardRun.checkWin(): Boolean {
    // If the other player still has at least one capture to be made,
    // current player turn can't win
    if (getAvalaibleCaptures(null, turn.other()).isNotEmpty()) {
        return false
    }
    moves
        // Retrieve the other player moves
        .filter { it.value.player === turn.other() }
        .keys.forEach { // For every square in a move:
            // Get valid squares to move to belonging to the other player
            if (retrieveValidSquaresToMoveTo(it, turn.other()).isNotEmpty()) {
                // Found at least one empty square where a valid play can
                // be made to
                return false
            }
        }
    // If it reaches this point the current turn opponent didn't have more checkers
    // to play, or the ones he/she had can't be moved
    return true
}

/**
 * Evaluates if a maximum limit of moves without a capture was reached.
 */
fun BoardRun.checkDraw() = this.mvsWithoutCapture >= MAX_MOVES_WITHOUT_CAPTURE