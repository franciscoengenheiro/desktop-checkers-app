package isel.leic.tds.checkers

import isel.leic.tds.checkers.model.*

/**
 * Retrieves the amount of regular checkers each player has at the start of the game,
 * according to the current board dimension: [BOARD_DIM]
 */
fun getPlayerPieces(dim: Int): Int = dim/2 * (dim/2 - 1)

/**
 * Validates if a string corresponds to a valid Square on the board
 * or throws [IllegalArgumentException] if it doesn't
 */
fun validateSqr(s: String): Square {
    require (BOARD_DIM < 10) { "Board dim is greater or equal to 10" }
    // Last validation is required in order to ensure given string has length 2.
    // For a BOARD_DIM = 10, one move could be "10e" which will be bigger than
    // the expected length
    require(s.length == 2) { "Invalid string format" }
    val sqr = s.toSquareOrNull()
    requireNotNull(sqr) { "Square $sqr does not exist on the board" }
    return sqr
}

/**
 * Receives a variable amount of strings, each one indicating a play, and plays them on
 * the board. If the strings are not in the expected format: <Square1> <Square2> <Player>
 * or if they're not valid then throws [IllegalArgumentException]
 * @return A new board with all the plays made
 */
fun Board.plays(vararg s: String): Board {
    // Format expected example: "<Square1> <Square2> <Player>"
    var b = this
    s.forEach {
        require(it.length == 7) { "Invalid string format" }
        val list = it.split(" ")
        require(list.size == 3) { "Incorrect expected arguments" }
        val toSqr = validateSqr(list[0])
        val fromSqr = validateSqr(list[1])
        val player = Player.valueOf(list[2])
        b = b.play(toSqr, fromSqr)
    }
    return b
}