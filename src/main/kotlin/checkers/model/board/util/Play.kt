package checkers.model.board.util

import checkers.model.board.Board
import checkers.model.board.BoardDraw
import checkers.model.board.BoardRun
import checkers.model.board.BoardWin
import checkers.model.moves.move.Checker
import checkers.model.moves.move.King
import checkers.model.moves.move.Player
import checkers.model.moves.move.Square

/**
 * Executes a play on the board only if a valid move was made. It also
 * evaluates if a draw or win condition was reached before returning the board.
 * Whoever calls this function must ensure both squares exist on the Board.
 * @param fromSqr Square to move a checker from.
 * @param toSqr Square to move a checker to.
 * @return Updated board with the valid move or capture performed by the player.
 * @throws [IllegalStateException] if this function is called on other board type
 * besides [BoardRun] or [IllegalArgumentException] - if the move requested isn't valid
 */
fun Board.play(fromSqr: Square, toSqr: Square): Board = when(this) {
    is BoardWin, is BoardDraw -> error("Game is over")
    is BoardRun -> {
        val checkerA: Checker? = moves[fromSqr]
        // Assert if the fromSqr has checker
        requireNotNull(checkerA) { "Square $fromSqr doesn't have a checker" }
        // Assert if the fromSqr has checker belonging to the current player turn
        require(checkerA.player === turn) { "Square $fromSqr doesn't have your checker" }
        val checkerB: Checker? = moves[toSqr]
        // Assert if the toSqr doesn't have a checker
        require(checkerB == null) { "Square $toSqr is occupied" }
        // Retrieve avalaible captures at the moment for the current player turn
        val listCaptures = getAvalaibleCaptures(prevCaptureSqr)
        var validMove = false
        // Define a Capture instance
        var foundCapture: Capture? = null
        // Evaluate if at least a capture was found:
        if (listCaptures.isNotEmpty()) {
            // Find a capture that matches the player move
            foundCapture = listCaptures.find { it.fromSqr == fromSqr && it.toSqr == toSqr }
            // Even if a capture is found, we have to make sure it matches the user
            // requested move
            requireNotNull(foundCapture) {
                "There is a mandatory capture in ${listCaptures.first().fromSqr}"
            }
            validMove = true
        } else {
            // Evaluates if the user requested move is valid, by checking if the toSqr is
            // a valid square to move to, knowing it's not a capture and just a regular move
            if (toSqr in retrieveValidSquaresToMoveTo(fromSqr))
                validMove = true
        }
        require(validMove) { "Invalid move" }
        // Increase the number of moves by 1 since the move is valid
        val n = numberOfMoves + 1
        // Remove the checker from the previous square
        var mvs = moves - fromSqr
        var movesWithoutCapture = mvsWithoutCapture
        if (foundCapture != null) {
            // Remove captured opponent checker from the board if a capture was found
            mvs -= foundCapture.rmvSqr
            movesWithoutCapture = 0 // Reset counter
        } else {
            movesWithoutCapture++ // Increase counter by 1
        }
        // Evaluate if the move was made to the last row of the board, according
        // to the current player turn. If it was and the checker isn't already a King,
        // crown it
        val lost_turn: Boolean
        if (checkIfOnlastRow(toSqr) && checkerA !is King) {
            // Add the previously made move to the board
            mvs += toSqr to King(turn)
            // Even if there are more captures to be made, when a Piece is upgraded to King,
            // current player turn is lost and the King can only be moved in the next turn
            lost_turn = true
        } else {
            // Add the previously made move to the board
            mvs += toSqr to checkerA
            lost_turn = false
        }
        // If a capture was made, evaluate if there's more mandatory captures to be made
        // from the landing position. If a capture wasn't made or if a King was crowned
        // there's no need to search for more captures
        val hasMoreCaptures = foundCapture != null && !lost_turn &&
                BoardRun(
                    mvs = mvs,
                    numberOfMoves = n,
                    mvsWithoutCapture = movesWithoutCapture,
                    prevCaptureSqr = foundCapture.toSqr,
                    turn = turn
                ).getAvalaibleCaptures(foundCapture.toSqr)
                 .isNotEmpty()
        when {
            !hasMoreCaptures &&
                    BoardRun(
                        mvs = mvs,
                        numberOfMoves = n,
                        mvsWithoutCapture = movesWithoutCapture,
                        prevCaptureSqr = foundCapture?.toSqr,
                        turn = turn
                    ).checkWin() -> BoardWin(mvs, turn)
            BoardRun(
                mvs = mvs,
                numberOfMoves = n,
                mvsWithoutCapture = movesWithoutCapture,
                prevCaptureSqr = foundCapture?.toSqr,
                turn = turn
            ).checkDraw() -> BoardDraw(mvs)
            else -> {
                if (hasMoreCaptures)
                    BoardRun(mvs, n, movesWithoutCapture, foundCapture?.toSqr, turn)
                else
                    BoardRun(mvs, n, movesWithoutCapture, null, turn.other())
            }
        }
    }
}

/**
 * Validates if [sqr] is on the last row of the board, according to
 * the current player turn.
 * @param sqr Square to evaluate.
 */
private fun BoardRun.checkIfOnlastRow(sqr: Square) =
    if (Player.w === turn) sqr.onLastRow else sqr.onFirstRow