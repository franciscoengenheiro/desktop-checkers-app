package isel.leic.tds.checkers.model

// Constants
const val BOARD_DIM = 8
const val MAX_SQUARES = BOARD_DIM * BOARD_DIM
const val MAX_MOVES_WITHOUT_CAPTURE = 20

/**
 * Represents a square with a checker on the board
 */
typealias Move = Pair<Square, Checker>

/**
 * Represents a list of all squares with a checker on the board
 */
typealias Moves = Map<Square, Checker>

/**
 * Board is a sealed class because this class can only have three instances,
 * which represent the possible types a board can be in the game.
 * Those types are: [BoardRun], [BoardWin], [BoardDraw].
 */
sealed class Board(val moves: Moves) {
    // A board is equal to another board if it has the same exact moves
    override fun equals(other: Any?) =
        other is Board && moves == other.moves
    override fun hashCode(): Int {
        return moves.hashCode()
    }
}

/**
 * Represents an instance of the board where it is possible to make plays.
 * @param mvs Represents all moves currently in the board (where the checkers are).
 * @param numberOfMoves Represents the total amount of moves made during the course
 * of the game.
 * @param mvsWithoutCapture Represents the total amount of moves made without
 * a capture, by both players.
 * @param prevCaptureSqr Represents the square where the previous capture landed.
 * @param turn Player who has permission to play in the board.
 */
class BoardRun(mvs: Moves,
               val numberOfMoves: Int,
               val mvsWithoutCapture: Int,
               val prevCaptureSqr: Square?,
               val turn: Player): Board(mvs)

/**
 * Represents an instance of the board where the game is finished, since
 * a player has won.
 * @param mvs Represents all moves currently in the board (where the checkers are).
 * @param winner Player who won the game.
 */
class BoardWin(mvs: Moves, val winner: Player): Board(mvs)

/**
 * Represents an instance of the board where the game finished in a draw.
 * @param mvs Represents all moves currently in the board (where the checkers are).
 */
class BoardDraw(mvs: Moves): Board(mvs)

/**
 * With a given [Square], retrieve information from the board, namely if it has a checker
 * or not (represented by null).
 */
operator fun Board.get(sqr: Square): Checker? = moves[sqr]

/**
 * Constructs initial board with all board pieces in their starting place:
 * white checkers at the bottom and black checkers at the top.
 */
fun initialBoard(): BoardRun {
    require(BOARD_DIM % 2 == 0) { "Board dim is not an even number" }
    var mvs: Moves = emptyMap()
    // For every square in the board:
    Square.values.forEach { sqr ->
        // Check if the current square is black, and it's not in the middle lines
        // of the board.
        if (sqr.black && sqr.row.index !in BOARD_DIM/2 - 1.. BOARD_DIM/2) {
            // Add to the already existing moves the correct piece.
            // With this implementation, white checkers will be at bottom of the board
            // while black checkers will be at the top.
            mvs += if (sqr.row.index > BOARD_DIM/2)
                sqr to Piece(Player.w)
            else
                sqr to Piece(Player.b)
        }
    }
    return BoardRun(mvs, 0, 0, null, Player.w)
}

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
                BoardRun(mvs, n, movesWithoutCapture, foundCapture.toSqr, turn)
                    .getAvalaibleCaptures(foundCapture.toSqr).isNotEmpty()
        when {
            !hasMoreCaptures &&
                    BoardRun(mvs, n, movesWithoutCapture, foundCapture?.toSqr, turn).checkWin()
                        -> BoardWin(mvs, turn)
            BoardRun(mvs, n, movesWithoutCapture, foundCapture?.toSqr, turn).checkDraw()
                        -> BoardDraw(mvs)
            else -> {
                if (hasMoreCaptures) BoardRun(mvs, n, movesWithoutCapture, foundCapture?.toSqr, turn)
                else BoardRun(mvs, n, movesWithoutCapture, null, turn.other())
            }
        }
    }
}

/**
 * Retrieves a List of all valid diagonal squares of [fromSqr] where a play can be made
 * from, according to the given [player]. This function only works for checker type:
 * [Piece].
 */
private fun BoardRun.getValidAdjacentSquaresToMoveTo(fromSqr: Square, player: Player) =
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
 * Retrieves a list of all empty diagonal squares from a [diagonalList].
 * This list is iterated diagonally and all empty squares are passed to the
 * return list, until it finds a square that has a checker.
 * @param diagonalList List of diagonal squares to search.
 */
private fun BoardRun.retrieveEmptyDiagonalSquaresInList(diagonalList: List<Square>): List<Square> {
    var finalList = emptyList<Square>()
    for (sqr in diagonalList) {
        if (moves[sqr] == null)
            // The square is empty, add it to the list
            finalList += sqr
        else
            // The square has a checker
            break
    }
    return finalList
}

/**
 * Retrieves a list of the valid squares where a move can be made to, starting in
 * [fromSqr]. This function assumes there are no more captures to be made for the
 * current board turn, since a non-capture move can only be done if there are no
 * captures to be made.
 * @param fromSqr Square to start the search.
 */
private fun BoardRun.retrieveValidSquaresToMoveTo(fromSqr: Square): List<Square> {
    val checker = moves[fromSqr]
    // Check if square has a checker
    if (checker != null) {
        return when (checker) {
            is Piece -> getValidAdjacentSquaresToMoveTo(fromSqr, turn)
            is King -> {
                // Limit King range of motion clock-wise in all the possible diagonal directions
                var finalList = retrieveEmptyDiagonalSquaresInList(fromSqr.upperBackSlash)
                finalList += retrieveEmptyDiagonalSquaresInList(fromSqr.upperSlash)
                finalList += retrieveEmptyDiagonalSquaresInList(fromSqr.lowerBackSlash)
                finalList += retrieveEmptyDiagonalSquaresInList(fromSqr.lowerSlash)
                finalList
            }
        }
    }
    // If it doesn't
    return emptyList()
}

/**
 * Validates if [sqr] is on the last row of the board, according to
 * the current player turn.
 */
private fun BoardRun.checkIfOnlastRow(sqr: Square) =
    if (Player.w === turn) sqr.onLastRow else sqr.onFirstRow

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
private fun BoardRun.getAvalaibleCaptures(
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
        // Retrieve current turn squares with a checker
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

/**
 * Returns a list of [Square]s where a move can be made to from [fromSqr].
 * @param fromSqr Square to start the search.
 */
fun BoardRun.getAvalaibleMoves(fromSqr: Square): List<Square> {
    // Retrieves all avalaible captures on the board for the current turn
    val listCaptures = getAvalaibleCaptures(prevCaptureSqr)
    // Retrieves all avalaible captures using fromSqr as the starting square
    val listCapturesWhereFromSqrIs = listCaptures.filter { it.fromSqr == fromSqr }
    return if (listCaptures.isNotEmpty() && listCapturesWhereFromSqrIs.isEmpty()) {
        // Returns an empty list since there are capture(s) to be made but fromSqr
        // does not represent a square where that capture can be done from
        emptyList()
    } else {
        // Keep only the squares that a checker can go to
        val newList: List<Square> = listCapturesWhereFromSqrIs.map { it.toSqr }
        // Returns a list of all squares a capture can be made from,
        // using fromSqr as the starting square or a list of all non-capture
        // moves that can be made from the same position
        newList.ifEmpty { retrieveValidSquaresToMoveTo(fromSqr) }
    }
}

/**
 * Using a square as input, this function returns a list of the first diagonal squares
 * that include an opponent's checker in all diagonals. Only adjacent diagonals from
 * the [centerSqr] are searched if the checker is a [Piece], but all diagonal squares
 * are searched if the checker is a [King].
 * @param centerSqr Square to start the search from.
 * @param checker Checker type which is on top of the [centerSqr] and will indicate the
 * type of search.
 */
private fun BoardRun.findOpponentCheckersInAllDiagonals(
    centerSqr: Square,
    checker: Checker
): List<Square> {
    requireNotNull(moves[centerSqr]) { "$centerSqr does not have a checker" }
    require(moves[centerSqr] == checker) { "$centerSqr does not have $checker on it" }
    // Retrieve a list of squares that have a checker from the other player,
    // according to the current turn given checker
    return when (checker) {
        is Piece -> {
            // Retrieve adjacent diagonal list
            centerSqr.adjacentDiagonalsList
        }
        is King -> {
            // Search all diagonals to find a square with a checker
            listOfNotNull(
                centerSqr.upperBackSlash.find { moves[it] != null },
                centerSqr.upperSlash.find { moves[it] != null },
                centerSqr.lowerBackSlash.find { moves[it] != null },
                centerSqr.lowerSlash.find { moves[it] != null }
            )
        }
    }.filter {
        // Filter only the checkers belonging to the other player
        moves[it]?.player === turn.other()
    }
}

/**
 * Finds a path to land in a diagonal direction after a capture.
 * Given two squares, one can infer the direction to go next by working with 2D vectors.
 * @param centerSqr Square where the capture started.
 * @param other Square where the opponent's checker is.
 * @return A list of squares where the square to land to can be
 * @throws [IllegalArgumentException] if the received squares are equal or
 * [other] is not a diagonal square of [centerSqr].
 */
private fun BoardRun.findDiagonalPathToLandTo(centerSqr: Square, other: Square): List<Square> {
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
 * Ensure the other player has at least one valid move to perform in the next turn.
 */
private fun BoardRun.checkWin(): Boolean {
    // If the other player still has at least one capture to be made,
    // current player turn can't win
    if (getAvalaibleCaptures(null, turn.other()).isNotEmpty())
        return false
    moves
        // Retrieve the other player moves
        .filter { it.value.player === turn.other() }
        .keys.forEach { // For every square in a move:
            // Get correspondent adjacent diagonal list:
            if (getValidAdjacentSquaresToMoveTo(it, turn.other()).isNotEmpty()) {
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
private fun BoardRun.checkDraw() = this.mvsWithoutCapture >= MAX_MOVES_WITHOUT_CAPTURE