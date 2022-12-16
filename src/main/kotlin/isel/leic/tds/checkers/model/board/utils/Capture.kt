package isel.leic.tds.checkers.model.board.utils

import isel.leic.tds.checkers.model.board.BoardRun
import isel.leic.tds.checkers.model.moves.move.*

/**
 * Represents a capture move.
 * @param fromSqr square where the capture started.
 * @param toSqr square where the capture ended.
 * @param rmvSqr square between them diagonally (jumped above).
 */
data class Capture(val fromSqr: Square, val toSqr: Square, val rmvSqr: Square)

/**
 * Evaluates for avalaible captures on the board depending on the value of
 * [previousCaptureSqr] and the received [turn].
 * All turn checkers are searched if it's value is null or if not, only the square
 * where the checker landed is evaluated.
 * @param previousCaptureSqr Previously made capture that indicates the type of search.
 * @param turn Player turn to evaluate. By default, is set to the current player turn.
 * @return A list of [Capture]s with the ones found.
 */
fun BoardRun.getAvalaibleCaptures(
    previousCaptureSqr: Square?,
    turn: Player = this.turn
): List<Capture> {
    // Create a list to store found captures
    var listCaptures = emptyList<Capture>()
    // Evaluate if the previous capture was made
    val turnMoves = if (previousCaptureSqr != null) {
        // If a single capture was made, only consider the square where the checker,
        // used for a previous capture, is
        mapOf(previousCaptureSqr to moves[previousCaptureSqr])
    } else {
        // Retrieve received turn squares with a checker
        moves.filter { it.value.player === turn }
    }
    // Search every checker for a valid capture
    turnMoves.forEach { (sqr, checker) ->
        findOpponentCheckersInAllDiagonals(centerSqr = sqr, checker as Checker)
            .forEach { dSqr -> // For every diagonal square in the list:
                // Find a path to land
                val path = findDiagonalPathToLandTo(centerSqr = sqr, other = dSqr)
                when (checker) {
                    is Piece -> {
                        // Only evaluate the first square in that path
                        if (path.isNotEmpty() && moves[path.first()] == null)
                        // A capture was found
                            listCaptures += Capture(sqr, path.first(), dSqr)
                    }
                    is King -> {
                        path.forEach {
                            // More than one capture possibility has been found
                            listCaptures += Capture(sqr, it, dSqr)
                        }
                    }
                }
            }
    }
    return listCaptures
}