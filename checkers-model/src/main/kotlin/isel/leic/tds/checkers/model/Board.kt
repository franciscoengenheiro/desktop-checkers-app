package isel.leic.tds.checkers.model

// Constants
const val BOARD_DIM = 8
const val MAX_SQUARES = BOARD_DIM * BOARD_DIM
const val MAX_MOVES_WITHOUT_CAPTURE = 10

/**
 * Represents the players of the game: [w] to identify the player with white checkers
 * and [b] to for the player with black checkers.
 */
enum class Player { w, b; // ; marks the end of the constants
    /**
     * Method to easily retrieve the other player
     */
    fun other() = if (this == w) b else w
}

// Typealiases provides alternative names for existing types and can also be applied to functions
// A Move will associate a square to a checker
typealias Move = Pair<Square, Checker>
// Moves will store all moves in a map for efficient retrieval purposes
typealias Moves = Map<Square, Checker>

// Board is a sealed class because this class can only three instances, which represent
// the possible types a board can have in the game
sealed class Board(val moves: Moves)

/**
 * Represents an instance of the board where it is possible to make plays.
 * @param mvs Represents all moves currently in the board (where the checkers are).
 * @param numberOfMoves Represents the toal amount of moves made during the course
 * of the game.
 * @param mvsWithoutCapture Represents the total amount of moves made without
 * a capture, by both players.
 * @param turn player who has permission to play in the board.
 */
class BoardRun(mvs: Moves,
               val numberOfMoves: Int,
               val mvsWithoutCapture: Int,
               val turn: Player): Board(mvs)

/**
 * Represents an instance of the board where the game is finished, since
 * a player has won.
 * @param mvs Represents all moves currently in the board (where the checkers are).
 * @param winner player who won the game
 */
class BoardWin(mvs: Moves, val winner: Player): Board(mvs)

/**
 * Represents an instance of the board where the game finished in a draw.
 * @param mvs Represents all moves currently in the board (where the checkers are).
 */
class BoardDraw(mvs: Moves): Board(mvs)

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
            // In this case, white checkers will be at bottom of the board
            // while black checkers will be at the top.
            mvs += if (sqr.row.index > BOARD_DIM/2)
                sqr to Piece(Player.w)
            else
                sqr to Piece(Player.b)
        }
    }
    return BoardRun(mvs, 0, 0, Player.w)
}

/**
 * With a given [Square], retrieve information from the board, namely if it has a checker
 * or not (represented by null).
 */
operator fun Board.get(sqr: Square): Checker? = moves[sqr]

/**
 * Executes a play on the board only if a valid move was made. It also
 * evaluates if a draw or win condition was reached before returning the board.
 * Whoever calls this function must ensure both squares exist on the Board.
 * @param fromSqr Square to move a checker from.
 * @param toSqr Square to move a checker to.
 * @return Updated board with the valid move or capture performed by the player.
 * If this function is called on other board type besides [BoardRun] throws [IllegalStateException]
 * or [IllegalArgumentException] if the move requested isn't valid
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
        val listCaptures = getAvalaibleCaptures()
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
                "There is a mandatory capture in ${listCaptures.first().fromSqr}" }
            validMove = true
        } else {
            // Evaluates if the user requested move is valid, depending on the checker type
            // and player, and knowing it's not a capture and just a regular move
            if (validateDiagonalMove(fromSqr, toSqr, checkerA))
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
            movesWithoutCapture++
        }
        // Evaluate if the move was made to the last row of the board, according
        // to the current player turn. If it was and the checker isn't already a King,
        // upgrade it
        val last_move: Move
        val lost_turn: Boolean
        if (checkIfOnlastRow(toSqr) && checkerA !is King) {
            // Add the previously made move to the board
            mvs += toSqr to King(turn)
            // Even if there are more captures to be made, when a Piece is upgraded to King,
            // current player turn is lost and can the King can only be moved in the next turn
            lost_turn = true
        } else {
            // Add the previously made move to the board
            mvs += toSqr to checkerA
            lost_turn = false
        }
        // If a capture was made, evaluate if there's more mandatory captures to be made
        // from the landing position, and if so keep the same player turn otherwise
        // return turn to the other player
        val hasMoreCaptures = BoardRun(mvs, n, movesWithoutCapture, turn)
                                .getAvalaibleCaptures().isNotEmpty()
                                    && foundCapture != null && !lost_turn
        when {
            !hasMoreCaptures && BoardRun(mvs, n, movesWithoutCapture, turn).checkWin()
                -> BoardWin(mvs, turn)
            BoardRun(mvs, n, movesWithoutCapture, turn).checkDraw() -> BoardDraw(mvs)
            else -> {
                if (hasMoreCaptures) BoardRun(mvs, n, movesWithoutCapture, turn)
                else BoardRun(mvs, n, movesWithoutCapture, turn.other())
            }
        }
    }
}

/**
 * Retrieves a List of all diagonal squares of [fromSqr], according to given [player].
 */
private fun getAdjacentDiagonals(fromSqr: Square, player: Player) =
    when (player) {
        // Retrieve only the diagonal squares above current fromSqr position
        Player.w -> fromSqr.adjacentDiagonalsList.filter { it.row.index < fromSqr.row.index }
        // Retrieve only the diagonal squares below current fromSqr position
        Player.b -> fromSqr.adjacentDiagonalsList.filter { it.row.index > fromSqr.row.index }
    }

/**
 * Retrieves a list of all empty diagonal squares from another [list].
 * This list is iterated and all empty squares are passed to the return list,
 * until it finds a square which has a checker.
 */
private fun BoardRun.retrieveEmptyDiagonalSquares(list: List<Square>): List<Square> {
    var finalList = emptyList<Square>()
    for (sqr in list) {
        if (moves[sqr] == null)
            // The square is empty
            finalList += sqr
        else
            // The square has a checker
            break
    }
    return finalList
}

/**
 * Validates if [toSqr] is a diagonal square of [fromSqr], according to
 * the received [checker] type. This function assumes [toSqr] is null and
 * there are no captures in the current board for this turn.
 */
private fun BoardRun.validateDiagonalMove(fromSqr: Square, toSqr: Square, checker: Checker) =
    when (checker) {
        is Piece -> toSqr in getAdjacentDiagonals(fromSqr, turn)
        is King -> {
            // Limit King range of motion clock-wise in all the possible diagonals types
            var finalList = retrieveEmptyDiagonalSquares(fromSqr.upperBackSlash)
            finalList += retrieveEmptyDiagonalSquares(fromSqr.upperSlash)
            finalList += retrieveEmptyDiagonalSquares(fromSqr.lowerBackSlash)
            finalList += retrieveEmptyDiagonalSquares(fromSqr.lowerSlash)
            toSqr in finalList
        }
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
 * Evaluates all current turn checkers for avalaible captures on the board
 * @return A list of [Capture] with the ones found
 */
fun BoardRun.getAvalaibleCaptures(): List<Capture> {
    // Create a list to store found captures
    var listCaptures = emptyList<Capture>()
    // Retrieve current turn squares with a checker
    val turnMoves = moves.filter { it.value.player === turn }
    // Search every checker for a valid capture
    turnMoves.forEach { (sqr, checker) ->
        // Retrieve a list of squares that have a checker from the other player,
        // according to the turn checker type
        val list = when (checker) {
            is Piece -> {
                // Find the other player checkers in the adjacent diagonal squares
                sqr.adjacentDiagonalsList
                    .filter {
                        // Evaluate if the checker belongs to the other player
                        moves[it]?.player === turn.other()
                    }
            }
            is King -> {
                // Search all diagonals to find a square with a checker
                listOfNotNull(
                    sqr.upperBackSlash.find { moves[it] != null },
                    sqr.upperSlash.find { moves[it] != null },
                    sqr.lowerBackSlash.find { moves[it] != null },
                    sqr.lowerSlash.find { moves[it] != null }
                ).filter {
                        // Filter only the other player checkers
                        moves[it]?.player === turn.other()
                }
            }
        }
        list.map { dSqr -> // For every diagonal square in the list:
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
 * Finds a path to land in a diagonal direction after a capture.
 * Given two squares, one can infer the direction to go next by working with 2D vectors.
 * @param centerSqr Square where the capture started.
 * @param other Square where the opponent's checker is.
 * @return A list of squares where the square to land to can be or throws
 * [IllegalArgumentException] if the squares are the same or [other] is not a diagonal
 * square of [centerSqr]
 */
private fun BoardRun.findDiagonalPathToLandTo(centerSqr: Square, other: Square): List<Square> {
    require(centerSqr !== other)
    { "Squares are equal, no diagonal direction can be calculated" }
    require(other in centerSqr.diagonalsList)
    { "$other square is not in one of $centerSqr diagonals" }
    val rowDiff = centerSqr.row.index - other.row.index
    val colDiff = centerSqr.column.index - other.column.index
    return if (rowDiff > 0 && colDiff > 0) retrieveEmptyDiagonalSquares(other.upperBackSlash)
    else if (rowDiff > 0 && colDiff < 0) retrieveEmptyDiagonalSquares(other.upperSlash)
    else if (rowDiff < 0 && colDiff < 0) retrieveEmptyDiagonalSquares(other.lowerBackSlash)
    else retrieveEmptyDiagonalSquares(other.lowerSlash)
}

/**
 * Ensure the other player has at least one valid move to perform in the next turn.
 */
private fun BoardRun.checkWin(): Boolean {
    // Retrieve the other player moves
    moves.filter { it.value.player == turn.other() }
        .keys.forEach { // For every square in a move:
            // Get correspondent diagonal list:
            getAdjacentDiagonals(it, turn.other())
                .forEach { sqr ->
                    // Evaluate if the square has a checker on it:
                    if (moves[sqr] == null) {
                        // If it doesn't, then a valid move can be made
                        return false
                    }
                }
        }
    // If it reaches this point the opponent didn't have more checkers to play, or the ones
    // he had can't be moved
    return true
}

/**
 * Evaluates if a maximum limit of moves without a capture was reached.
 */
private fun BoardRun.checkDraw() = this.mvsWithoutCapture >= MAX_MOVES_WITHOUT_CAPTURE