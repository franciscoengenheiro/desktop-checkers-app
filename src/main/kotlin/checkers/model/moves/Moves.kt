package checkers.model.moves

import checkers.model.moves.move.Checker
import checkers.model.moves.move.Square

/**
 * Represents a [Square] with a [Checker] on the board.
 */
typealias Move = Pair<Square, Checker>

/**
 * Represents a list of all [Square]s with a [Checker] on the board.
 */
typealias Moves = Map<Square, Checker>