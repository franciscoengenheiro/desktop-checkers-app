package checkers.model.moves.move

import checkers.model.board.BOARD_DIM
import checkers.model.board.MAX_SQUARES
import checkers.model.moves.square.Column
import checkers.model.moves.square.Row
import checkers.model.moves.square.indexToColumn
import checkers.model.moves.square.indexToRow

/**
 * Represents a square instance defined by a unique combination of a [Row] and [Column].
 */
class Square private constructor(private val r: Row, private val c: Column) {
    // Class properties initialized with getters, so that their value is only calculated
    // when the property is called
    val row get() = r // Example: 1, 2, 3, ...
    val column get() = c // Example: a, b, c, ...

    // If an index sum (column and row) is even then this Square is black otherwise is white
    val black get() = (r.index + c.index) % 2 != 0

    // Retrieves a list of all adjacent diagonal squares in proximity of this Square instance
    val adjacentDiagonalsList get() =
        values
            .filter { (r.index == it.row.index + 1) || (r.index == it.row.index - 1) }
            .filter { (c.index == it.column.index + 1) || (c.index == it.column.index - 1) }

    // Retrieves a list of all diagonal squares in the same slash or backslash of this
    // Square instance, excluding itself
    val diagonalsList get() =
        values
            .filter {
                (
                    r.index + c.index == it.row.index + it.column.index || // blackslash
                        r.number + c.symbol.code == it.row.number + it.column.symbol.code
                    )
            } // slash
            .filterNot { r.number == it.row.number && c.symbol == it.column.symbol } // exclude self

    /*                         Labels:
     * [X] [-] [-] [-] [S]     C - Center Square (this square instance)
     * [-] [X] [-] [S] [-]     X - Upper backslash squares of C
     * [-] [-] [C] [-] [-]     S - Upper slash squares of C
     * [-] [s] [-] [x] [-]     x - Lower backslash squares of C
     * [s] [-] [-] [-] [x]     s - Lower slash squares of C
     */
    // Retrieves a list of all the squares in the upper backslash of this Square instance
    val upperBackSlash get() =
        diagonalsList
            .filter { it.row.index < r.index && it.column.index < c.index }
            .reversed() // This list must be sorted by the shortest distance from the center square,

    // since the Squares are created from top to bottom, the "upper" lists
    // need to be reversed
    // Retrieves a list of all the squares in the upper slash of this Square instance
    val upperSlash get() =
        diagonalsList
            .filter { it.row.index < r.index && it.column.index > c.index }
            .reversed()

    // Retrieves a list of all the squares in the lower backslash of this Square instance
    val lowerBackSlash get() =
        diagonalsList
            .filter { it.row.index > r.index && it.column.index > c.index }

    // Retrieves a list of all the squares in the lower slash of this Square instance
    val lowerSlash get() =
        diagonalsList
            .filter { it.row.index > r.index && it.column.index < c.index }

    // Evaluate if this Square instance is in the first row
    val onFirstRow get() = r.index == BOARD_DIM - 1

    // Evaluate if this Square instance is in the last row
    val onLastRow get() = r.index == 0

    // Declare an object to store the only instances this class can have.
    // This object is created once, and every instance of this class has access to it,
    // but they cannot alter it since the constructor is private
    companion object {
        // If MAX_SQUARES == 4, values = [Square(2, a), Square(2, b), Square(1, a), Square(1, b)]
        val values = List(MAX_SQUARES) { idx ->
            Square((idx / BOARD_DIM).indexToRow(), (idx % BOARD_DIM).indexToColumn())
        }

        // Since this class constructor is private, meaning we don't want to let the user
        // create more squares besides the already existing unique ones, it was considered useful
        // to have a public way to call the class as if the constructor wasn't private, although
        // invoke function can be used for other applications besides that
        /**
         * Retrieves a Square instance from the given row and column indexes.
         * @param rowIndex The row index of the square.
         * @param colIndex The column index of the square.
         * @return The Square instance.
         */
        operator fun invoke(rowIndex: Int, colIndex: Int) = values[rowIndex * BOARD_DIM + colIndex]

        operator fun invoke(r: Row, c: Column) = values[r.index * BOARD_DIM + c.index]

    }

    // Example: Square(2, a) = "2a"
    override fun toString() = "${row.number}${column.symbol}"
}

// Extension functions:
/**
 * Evaluates if the given string is a match for a valid square.
 * @return The square it belongs to or null if none of the squares match.
 */
fun String.toSquareOrNull() = Square.values.firstOrNull { sqr -> sqr.toString() == this }
