package isel.leic.tds.checkers.model.moves

import isel.leic.tds.checkers.model.moves.move.Checker
import isel.leic.tds.checkers.model.moves.move.Square

/**
 * Represents a square with a checker on the board
 */
typealias Move = Pair<Square, Checker>

/**
 * Represents a list of all squares with a checker on the board
 */
typealias Moves = Map<Square, Checker>