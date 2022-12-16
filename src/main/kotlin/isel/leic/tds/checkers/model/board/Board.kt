package isel.leic.tds.checkers.model.board

import isel.leic.tds.checkers.model.moves.Moves
import isel.leic.tds.checkers.model.moves.move.*

// Constants
val BOARD_DIM_POSSIBLE_VALUES = listOf(8, 10)
var BOARD_DIM: Int = 8
    /*private set(value) {
        field = if (value in BOARD_DIM_POSSIBLE_VALUES) value
                else throw IllegalArgumentException(
                    "Board dim value $value is invalid")
    }*/


val MAX_SQUARES = BOARD_DIM * BOARD_DIM
const val MAX_MOVES_WITHOUT_CAPTURE = 20

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
               val turn: Player
): Board(mvs)

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