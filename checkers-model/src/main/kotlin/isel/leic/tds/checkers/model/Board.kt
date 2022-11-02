package isel.leic.tds.checkers.model

// Reminder: const val usage might cause problems when it's corresponding value is changed
// but all the files referencing it are not, resulting in unexpected errors
// Constants:
const val BOARD_DIM = 8
const val MAX_SQUARES = BOARD_DIM * BOARD_DIM
const val MAX_MOVES_WITHOUT_CAPTURE = 20

/**
 * Represents the players of the game: [w] to identify the player with white checkers
 * and [b] to for the player with black checkers
 */
enum class Player { w, b; // ; marks the end of the constants
    // Method to easily retrieve the other player
    fun other() = if (this == w) b else w
}

// Typealiases provides alternative names for existing types and can also be applied to functions
typealias Move = Pair<Square, Checker>
// Moves will store all moves in a map for efficient retrival purposes
typealias Moves = Map<Square, Checker>

// According to kotlinlang.org: "A sealed class represent restricted class hierarchies
// that provide more control over inheritance. All direct subclasses of a sealed class
// are known at compile time. No other subclasses may appear outside a module within
// which the sealed class is defined".
sealed class Board(val moves: Moves)

/**
 * Represents an instance of the board where it is possible to make plays
 * @param mvs Represents all avalaible moves currently in the board
 * @param movesWithoutCapture Represents the total moves made by both players without a capture
 * @param turn Current player which has permission to play in the board
 */
class BoardRun(mvs: Moves, val movesWithoutCapture: Int, val turn: Player): Board(mvs)

/**
 * Represents an instance of the board where the game is finished since
 * a player won
 * @param mvs Represents all avalaible moves currently in the board
 * @param winner player who won the game
 */
class BoardWin(mvs: Moves, val winner: Player): Board(mvs)

/**
 * Represents an instance of the board where the game finished in a draw
 * @param mvs Represents all avalaible moves currently in the board
 */
class BoardDraw(mvs: Moves): Board(mvs)

/**
 * Constructs initial board when a new game is started
 * @return A board with all starting pieces in place: white checkers at the
 * bottom and black checkers at the top
 */
fun initialBoard(): BoardRun {
    require(BOARD_DIM % 2 == 0) { "BOARD_DIM is not an even number" }
    var mvs: Moves = emptyMap()
    // For every square in the board:
    Square.values.forEach { sqr ->
        // Check if the current square is black, and it's not in the middle lines
        // of the board.
        if (sqr.black && sqr.row.index !in BOARD_DIM/2 - 1.. BOARD_DIM/2) {
            // Add to the already existing moves the correct piece
            // In this case, white checkers will be at bottom of the board
            // while black checkers will be at the top
            mvs += if (sqr.row.index > BOARD_DIM/2)
                sqr to Piece(Player.w)
            else
                sqr to Piece(Player.b)
        }
    }
    return BoardRun(mvs,0, Player.w)
}

/**
 * With a given [sqr], retrieve information from the board, namely if it has a checker
 * or not (represented by null)
 */
operator fun Board.get(sqr: Square): Checker? = moves[sqr]

/**
 * Executes a play on the board only if a valid move was made. It also
 * evaluates if a draw or win condition was reached, before returning the board.
 * Whoever calls this function must ensure both squares exist on the Board
 * @param fromSqr Square to move a checker from
 * @param toSqr Square to move a checker to
 * @param player Player calling this function
 * @return Updated board with the valid move or capture performed by the player.
 * If this function is called on other board type besides BoardRun throws [IllegalStateException]
 * or [IllegalArgumentException] if the move requested isn't valid
 */
fun Board.play(fromSqr: Square, toSqr: Square, player: Player): Board = when(this) {
    is BoardWin, is BoardDraw -> error("Game is over")
    is BoardRun -> {
        // Assert if it's the turn of the player who called this function
        require(turn == player) { "Not your turn" }
        // Assert if the fromSqr has checker belonging to the current player turn
        val checkerA: Checker? = moves[fromSqr]
        requireNotNull(checkerA) { "Square $fromSqr doesn't have a checker" }
        require(checkerA.player == player) { "Square $fromSqr doesn't have your checker" }
        // Assert if the toSqr doesn't have a checker
        val checkerB: Checker? = moves[toSqr]
        require(checkerB == null) { "Position $toSqr is occupied" }
        // Retrieve avalaible captures at the moment for the current player turn
        val listCaptures = this.getAvalaibleCaptures()
        var validMove: Boolean = false
        var foundCapture: Capture? = null
        if (listCaptures.isNotEmpty()) {
            // Find a capture that matches the player move
            foundCapture = listCaptures.find { it.fromSqr == fromSqr && it.toSqr == toSqr }
            requireNotNull(foundCapture) {
                "There is a mandatory capture in ${listCaptures.first().fromSqr}" }
            validMove = true
        } else {
            if (validateOneDiagonalMove(fromSqr, toSqr, checkerA))
                validMove = true
        }
        require(validMove) { "Invalid move" }
        // Remove current player turn checker from the previous position
        var mvs = moves - fromSqr
        // Remove captured opponent checker from the board if a capture was found
        var movesWithoutCapture = this.movesWithoutCapture
        if (foundCapture != null) {
            mvs -= foundCapture.rmvSqr
            movesWithoutCapture = 0
        } else {
            movesWithoutCapture++
        }
        // Evaluate if the move was made to the last row of the board, according
        // to the current player turn. If it was and the checker isn't already a King,
        // upgrade it
        val last_move = if (checkIfOnlastRow(toSqr) && checkerA !is King) toSqr to King(player)
                        else toSqr to checkerA
        // Add the previously made move to the board
        mvs += last_move
        when {
            BoardRun(mvs, movesWithoutCapture, player).checkWin() -> BoardWin(mvs, player)
            BoardRun(mvs, movesWithoutCapture, player).checkDraw() -> BoardDraw(mvs)
            else -> {
                // If a capture was made, evaluate if there's more mandatory captures to be made,
                // and if so keep the same player turn otherwise return turn to the other player
                val p = if (BoardRun(mvs, movesWithoutCapture, player).getAvalaibleCaptures().isNotEmpty()
                    && foundCapture != null) player else player.other()
                BoardRun(mvs, movesWithoutCapture, p)
            }
        }
    }
}

/**
 * Retrieves a list<[Square]> of all diagonal squares of [fromSqr], according to
 * given [player]
 */
private fun getDiagonalList(fromSqr: Square, player: Player) =
    when (player) {
        // Retrieve only the diagonal squares above current fromSqr position
        Player.w -> fromSqr.diagonaList.filter { it.row.index < fromSqr.row.index }
        // Retrieve only the diagonal squares below current fromSqr position
        Player.b -> fromSqr.diagonaList.filter { it.row.index > fromSqr.row.index }
    }

/**
 * Validates if [toSqr] is a diagonal square of [fromSqr], according to
 * the received [checker] type
 */
private fun BoardRun.validateOneDiagonalMove(fromSqr: Square, toSqr: Square, checker: Checker) =
    when (checker) {
        is Piece -> toSqr in getDiagonalList(fromSqr, turn)
        is King -> toSqr in fromSqr.diagonaList
    }

/**
 * Validates if [sqr] is on the last row of the board, according to
 * the current player turn
 */
private fun BoardRun.checkIfOnlastRow(sqr: Square) =
    if (Player.w == turn) sqr.onLastRow else sqr.onFirstRow

/**
 * Represents a capture move
 * @param fromSqr square where the capture started
 * @param toSqr square where the capture ended
 * @param rmvSqr square between them diagonally (jumped above)
 */
private data class Capture(val fromSqr: Square, val toSqr: Square, val rmvSqr: Square)

/**
 * Evaluates all current player turn checkers for avalaible captures on the board
 * @return A list<[Capture]> with the ones found
 */
private fun BoardRun.getAvalaibleCaptures(): List<Capture> {
    // Search every current player checker for a valid capture
    var listCaptures = emptyList<Capture>()
    moves.keys
        .filter { moves[it]?.player == turn } // Retrieve current player turn squares with a checker
        .forEach { sqr ->
            // Find proximity checkers (in square diagonal) from the other player
            sqr.diagonaList
                .filter {
                    // Evaluate if the checker belongs to the other player
                    moves[it]?.player == turn.other()
                }
                .mapNotNull { dSqr ->
                    val foundSqr = dSqr.diagonaList.find {
                        // If both squares (current square and it's diagonal are on the
                        // same slash) exisgt, then the square to complete the capture
                        // is also on this slash
                        // Condition (it != sqr) ensures the found square doesn't go back
                        // to the square where the jump was made from
                        if (sqr.onTheSameSlashOf(dSqr)) {
                            it.onTheSameSlashOf(dSqr) && it != sqr
                        } else {
                            !it.onTheSameSlashOf(dSqr) && it != sqr
                        }
                    }
                    // If the square exists and has a piece:
                    if (moves[foundSqr] == null && foundSqr != null)
                        // A valid capture was found
                        Capture(sqr, foundSqr, dSqr)
                    else
                        null
                }
                .forEach { listCaptures += it } // Add found captures
            }
    return listCaptures
}

/**
 * Ensure the other player has at least one valid move to perform
 */
private fun BoardRun.hasAValidMove(): Boolean {
    moves.filter { it.value.player == turn.other() } // Retrieve the other player moves
        .keys.forEach { // For every square in a move:
            // Get correspondent diagonal list:
            getDiagonalList(it, turn.other()).forEach { sqr ->
                // Evaluate if the square has a checker on it:
                if (moves[sqr] == null) {
                    // If it doesn't, then a valid move can be made
                    return true
                }
            }
        }
    return false
}

/**
 * Evaluates if the current player turn has won the game by either capturing all the
 * opponent checkers or by blocking the opponent remaining checker(s)
 */
private fun BoardRun.checkWin() = !hasCheckers() || !hasAValidMove()

/**
 * Evaluates if the other player still has checkers avalaible to play
 */
private fun BoardRun.hasCheckers() = moves.any { it.value.player == turn.other() }

/**
 * Evaluates if a maximum limit of valid moves without a capture was reached
 */
private fun BoardRun.checkDraw() = this.movesWithoutCapture == MAX_MOVES_WITHOUT_CAPTURE