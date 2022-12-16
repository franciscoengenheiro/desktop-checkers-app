package isel.leic.tds.checkers

import isel.leic.tds.checkers.model.board.BOARD_DIM
import isel.leic.tds.checkers.model.board.Board
import isel.leic.tds.checkers.model.board.BoardRun
import isel.leic.tds.checkers.model.board.utils.play
import isel.leic.tds.checkers.model.moves.move.*

/**
 * Receives a variable amount of strings, each one indicating a square, and returns a list
 * of all given squares.
 */
fun squaresToListOf(vararg s: String) =
    s.fold(listOf<Square>()) { list, sqr ->
        list + validateSqr(sqr)
    }

/**
 * Retrieves the amount of regular checkers each player has at the start of the game,
 * according to the current board dimension: [BOARD_DIM].
 */
fun getPlayerPieces(dim: Int): Int = dim/2 * (dim/2 - 1)

/**
 * Receives a variable amount of strings, each one indicating a play, and plays them
 * on the board. If the strings are not in the expected format or if the squares they
 * represent are not valid then throws [IllegalArgumentException].
 * @return A new board updated with all the plays made.
 */
fun Board.plays(vararg s: String) =
    // Format expected example: "<Square1> <Square2>"
    s.fold(this) { board, string ->
        val list = string.split(" ")
        require(list.size == 2) { "Incorrect expected arguments" }
        val toSqr = validateSqr(list[0])
        val fromSqr = validateSqr(list[1])
        board.play(toSqr, fromSqr)
    }

/**
 * Receives a variable amount of strings, each one indicating a move, and places them
 * on the board. See [createMoves] for more details.
 * @param set_turn selects the player turn to start the board with.
 * @return Creates a new board with only the moves requested.
 */
fun createPersonalizedBoard(set_turn: Player, vararg s: String): BoardRun {
    require(BOARD_DIM % 2 == 0) { "Board dim is not an even number" }
    // *s indicates a pointer to the first variable argument received
    val mvs = createMoves(*s)
    return BoardRun(mvs, mvs.size, 0, null, set_turn)
}

/**
 * Receives a variable amount of strings, each one indicating a move, and places them
 * on the board. If the strings are not in the expected format or if the squares or checkers
 * they represent are not valid then throws [IllegalArgumentException].
 * Example: "2a w" corresponds to Pair<Square(2, a), Piece(Player.w)>
 * @return A list of moves with only the moves requested.
 */
fun createMoves(vararg s: String) =
    // Expected format: <Square> <Checker>
    s.fold(emptyMap<Square, Checker>()) { mvs, string ->
        require(string.length == 4) { "Incorrect expected arguments" }
        val list = string.split(" ")
        require(list.size == 2) { "Incorrect expected arguments" }
        val sqr = validateSqr(list[0])
        require(sqr.black) { "$sqr is not a black square" }
        val player_name = list[1].first()
        val player: Player
        try {
            player = Player.valueOf(player_name.lowercase())
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid player")
        }
        mvs + when (player_name) {
            'w' -> sqr to Piece(player)
            'b' -> sqr to Piece(player)
            'W' -> sqr to King(player)
            'B' -> sqr to King(player)
            else -> throw IllegalArgumentException("Invalid checker")
        }
    }

/**
 * Validates if a string corresponds to a valid Square on an already created board
 * or throws [IllegalArgumentException] if it doesn't.
 */
fun validateSqr(s: String): Square {
    val sqr = s.toSquareOrNull()
    requireNotNull(sqr) { "Square $sqr does not exist on the board" }
    return sqr
}