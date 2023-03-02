package checkers.model.board

import checkers.model.moves.Moves
import checkers.model.moves.move.*
import org.litote.kmongo.cond

// Constants
val BOARD_DIM = setActualBoardDimension()
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
 * @throws IllegalArgumentException If the board dimension is not an even number.
 */
fun initialBoard(): BoardRun {
    require(BOARD_DIM % 2 == 0) { "Board dim is not an even number" }
    return BoardRun(
        mvs = populateBoard(),
        numberOfMoves = 0,
        mvsWithoutCapture = 0,
        prevCaptureSqr = null,
        turn = Player.w
    )
}

/**
 * Populates the board within a given condition. See [Condition] for more details.
 * @param condition indicates what condition the board should start with.
 * Has a default value that represents the normal checkers placement.
 * @return The chosen [Moves] to start the board with.
 */
private fun populateBoard(condition: Condition = Condition.DEFAULT): Moves {
    var mvs: Moves = emptyMap()
    when(condition) {
        Condition.FAST_WIN -> {
            mvs += Square(0,1) to King(Player.w)
            mvs += Square(5,6) to King(Player.b)
        }
        Condition.FAST_WIN_BY_BLOCKING -> {
            mvs += Square(3,2) to Piece(Player.b)
            mvs += Square(3,0) to Piece(Player.b)
            mvs += Square(6,1) to Piece(Player.w)
        }
        Condition.FAST_DRAW -> {
            mvs += Square(6,0) to King(Player.w)
            mvs += Square(5,6) to King(Player.b)
        }
        Condition.DEFAULT -> Square.values.forEach { sqr ->
            // Check if the current square is black, and it's not in the middle lines
            // of the board.
            if (sqr.black && sqr.row.index !in BOARD_DIM / 2 - 1..BOARD_DIM / 2) {
                // Add to the already existing moves the correct piece.
                // With this implementation, white checkers will be at bottom of the board
                // while black checkers will be at the top.
                mvs += if (sqr.row.index > BOARD_DIM / 2)
                    sqr to Piece(Player.w)
                else
                    sqr to Piece(Player.b)
            }
        }
    }
    return mvs
}

/**
 * Represents a board starting condition. This condition is related to how the board
 * checkers should be placed in order to achieve specific game states faster.
 */
enum class Condition {
    DEFAULT,
    FAST_WIN,
    FAST_WIN_BY_BLOCKING,
    FAST_DRAW
}