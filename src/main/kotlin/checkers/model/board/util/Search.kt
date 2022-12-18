package checkers.model.board.util

import checkers.model.board.BoardRun
import checkers.model.board.get
import checkers.model.moves.move.*

/**
 * Using a square as input, this function returns a list of the first diagonal squares
 * that include an opponent's checker in all diagonals. Only adjacent diagonals squares
 * from the [centerSqr] are searched if the checker is a [Piece], but all diagonal squares
 * are searched if the checker is a [King].
 * @param centerSqr Square to start the search from.
 * @param checker Checker type which is on top of the [centerSqr] and will indicate the
 * type of search.
 */
fun BoardRun.findOpponentCheckersInAllDiagonals(
    centerSqr: Square,
    checker: Checker
): List<Square> {
    requireNotNull(moves[centerSqr]) { "$centerSqr does not have a checker" }
    require(moves[centerSqr] == checker) { "$centerSqr does not have $checker on it" }
    // Retrieve a list of squares that have a checker from the other player,
    // according to the current turn given checker
    return when (checker) {
        is Piece -> {
            // Retrieve adjacent diagonal squares list
            centerSqr.adjacentDiagonalsList
                .filter { isOtherPlayerChecker(it) }
        }
        is King -> {
            // Search all diagonals to find a square with a checker
            listOfNotNull(
                retrieveFirstOpponentCheckerOrNull(centerSqr.upperBackSlash),
                retrieveFirstOpponentCheckerOrNull(centerSqr.upperSlash),
                retrieveFirstOpponentCheckerOrNull(centerSqr.lowerBackSlash),
                retrieveFirstOpponentCheckerOrNull(centerSqr.lowerSlash)
            )
        }
    }
}

/**
 * Evaluates if the received square has a checker from the other player
 * on top of it.
 * @param sqr Square to search
 */
private fun BoardRun.isOtherPlayerChecker(sqr: Square): Boolean {
    val checker = moves[sqr]
    return checker != null && checker.player === turn.other()
}

/**
 * Retrieves only the first opponent checker found in a diagonal list.
 * If turn checker is found first, returns null.
 * @param diagonalList list to search
 */
private fun BoardRun.retrieveFirstOpponentCheckerOrNull(diagonalList: List<Square>): Square? {
    for (sqr in diagonalList) {
        val checker = moves[sqr]
        // Find the first checker ocurrence in the list
        if (checker != null) {
            // Check whose player belongs to
            return when(checker.player) {
                turn -> null
                turn.other() -> sqr
                else -> break
            }
        }
    }
    return null
}

/**
 * Returns a list of [Square]s where a move can be made to from, starting in [fromSqr].
 * @param fromSqr Square to start the search.
 */
fun BoardRun.getAvalaibleMoves(fromSqr: Square): List<Square> {
    // Retrieves all avalaible captures on the board for the turn,
    // according to the current value of the previous capture
    val listCaptures = getAvalaibleCaptures(prevCaptureSqr)
    // Retrieves all avalaible captures using fromSqr as the starting square
    val listCapturesWhereFromSqrIs = listCaptures.filter { it.fromSqr == fromSqr }
    return if (listCaptures.isNotEmpty() && listCapturesWhereFromSqrIs.isEmpty()) {
        // Returns an empty list since there is at least one capture to be made but
        // fromSqr does not represent a square where that capture can be done from
        emptyList()
    } else {
        val newList: List<Square> = listCapturesWhereFromSqrIs.map { it.toSqr }
        // Returns a list of all squares a capture can be made from,
        // using fromSqr as the starting square, or a list of all non-capture
        // moves that can be made from the same position
        newList.ifEmpty { retrieveValidSquaresToMoveTo(fromSqr) }
    }
}

/**
 * Finds a path to land in a diagonal direction after a capture.
 * Given two squares, one can infer the direction to go next by working with 2D vectors.
 * @param centerSqr Square where the capture started.
 * @param other Square where the opponent's checker is.
 * @return A list of squares where the square to land to can be
 * @throws [IllegalArgumentException] If the received squares are equal or
 * [other] is not a diagonal square of [centerSqr].
 */
fun BoardRun.findDiagonalPathToLandTo(centerSqr: Square, other: Square): List<Square> {
    require(centerSqr !== other)
    { "Squares are equal, no diagonal direction can be calculated" }
    require(other in centerSqr.diagonalsList)
    { "$other square is not in one of $centerSqr diagonals" }
    val rowDiff = centerSqr.row.index - other.row.index
    val colDiff = centerSqr.column.index - other.column.index
    return if (rowDiff > 0 && colDiff > 0) retrieveEmptyDiagonalSquaresInList(other.upperBackSlash)
    else if (rowDiff > 0 && colDiff < 0) retrieveEmptyDiagonalSquaresInList(other.upperSlash)
    else if (rowDiff < 0 && colDiff < 0) retrieveEmptyDiagonalSquaresInList(other.lowerBackSlash)
    else retrieveEmptyDiagonalSquaresInList(other.lowerSlash)
}

/**
 * Retrieves a list of the valid squares where a move can be made to, starting in
 * [fromSqr]. This function assumes there are no more captures to be made for the
 * current board turn, since a non-capture move can only be done if there are no
 * captures to be made.
 * @param fromSqr Square to start the search.
 * @param turn Player turn to evaluate. By default, is set to the current player turn.
 */
fun BoardRun.retrieveValidSquaresToMoveTo(
    fromSqr: Square,
    turn: Player = this.turn
): List<Square> {
    val checker = moves[fromSqr]
    // Check if square has a checker
    return if (checker != null) {
        when (checker) {
            is Piece -> getValidAdjacentSquaresToMoveTo(fromSqr, turn)
            is King -> getValidDiagonalSquaresToMoveTo(fromSqr)
        }
    } else {
        // If it doesn't
        emptyList()
    }
}

/**
 * Retrieves a List of all valid adjacent diagonal squares of [fromSqr] where a
 * play can be made from, according to the given [player] to assert which squares
 * to filter.
 * This function should only be called for checker type: [Piece], for checker type
 * [King] see [getValidDiagonalSquaresToMoveTo] function.
 */
fun BoardRun.getValidAdjacentSquaresToMoveTo(fromSqr: Square, player: Player) =
    when (player) {
        // Retrieve only the adjacent diagonal squares above current fromSqr
        // position that do not have a checker on top
        Player.w -> fromSqr.adjacentDiagonalsList
            .filter { it.row.index < fromSqr.row.index }
            .filter { this[it] == null }
        // Retrieve only the adjacent diagonal squares below current fromSqr
        // position that do not have a checker on top
        Player.b -> fromSqr.adjacentDiagonalsList
            .filter { it.row.index > fromSqr.row.index }
            .filter { this[it] == null }
    }

/**
 * Retrieves a List of all valid diagonal squares of [fromSqr] where a play can be made
 * from. This function should only be called for checker type: [King], for [Piece] see
 * [getValidAdjacentSquaresToMoveTo] function.
 */
fun BoardRun.getValidDiagonalSquaresToMoveTo(fromSqr: Square): List<Square> {
    // Limit King range of motion clock-wise in all the possible diagonal directions
    var finalList = retrieveEmptyDiagonalSquaresInList(fromSqr.upperBackSlash)
    finalList += retrieveEmptyDiagonalSquaresInList(fromSqr.upperSlash)
    finalList += retrieveEmptyDiagonalSquaresInList(fromSqr.lowerBackSlash)
    finalList += retrieveEmptyDiagonalSquaresInList(fromSqr.lowerSlash)
    return finalList
}

/**
 * Retrieves a list of all empty diagonal squares from a [diagonalList].
 * This list is iterated diagonally and all empty squares are passed to the
 * return list, until it finds a square that has a checker.
 * @param diagonalList List of diagonal squares to search.
 */
private fun BoardRun.retrieveEmptyDiagonalSquaresInList(
    diagonalList: List<Square>
): List<Square> {
    var finalList = emptyList<Square>()
    for (sqr in diagonalList) {
        // If the square is empty, add it to the list
        if (moves[sqr] == null) finalList += sqr
        // The square has a checker
        else break
    }
    return finalList
}