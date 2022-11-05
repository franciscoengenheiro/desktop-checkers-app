package isel.leic.tds.checkers

import isel.leic.tds.checkers.model.*

/**
 * Receives a variable amount of strings, each one indicating a square, and returns a list
 * of all given squares
 */
fun squaresToListOf(vararg s: String): List<Square> {
    var list = emptyList<Square>() // fold?
    s.forEach { list += validateSqr(it) }
    return list
}

/**
 * Retrieves the amount of regular checkers each player has at the start of the game,
 * according to the current board dimension: [BOARD_DIM]
 */
fun getPlayerPieces(dim: Int): Int = dim/2 * (dim/2 - 1)

/**
 * Receives a variable amount of strings, each one indicating a play, and plays them
 * on the board. If the strings are not in the expected format or if the squares they
 * represent are not valid then throws [IllegalArgumentException]
 * @return A new board with all the plays made
 */
fun Board.plays(vararg s: String): Board {
    // Format expected example: "<Square1> <Square2> <Player>"
    var b = this
    s.forEach { // fold?
        val list = it.split(" ")
        require(list.size == 2) { "Incorrect expected arguments" }
        val toSqr = validateSqr(list[0])
        val fromSqr = validateSqr(list[1])
        b = b.play(toSqr, fromSqr)
    }
    return b
}

/**
 * Validates if a string corresponds to a valid Square or throws
 * [IllegalArgumentException] if it doesn't
 */
fun validateSqr(s: String): Square {
    val sqr = s.toSquareOrNull()
    requireNotNull(sqr) { "Square $sqr does not exist on the board" }
    return sqr
}