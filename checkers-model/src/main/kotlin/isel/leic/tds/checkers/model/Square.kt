package isel.leic.tds.checkers.model

class Square private constructor(val r: Row, val c: Column) {
    // Class properties initialized with getters, so that their value is only calculated
    // when the property is called
    val row get() = r // Example: 1, 2, 3, ...
    val column get() = c // Example: a, b, c, ...
    // if index sum (column and row) is even then square is black otherwise is white
    val black get() = (r.index + c.index) % 2 != 0
    // Create a list of all diagonal black squares in proximity of this Square instance
    val diagonaList get() =
        values
            .filter { (r.index == it.row.index + 1) || (r.index == it.row.index - 1) }
            .filter { (c.index == it.column.index + 1) || (c.index == it.column.index - 1) }
    // Evaluate if this Square instance is in the first
    val onFirstRow get() = r.index == BOARD_DIM - 1
    // Evaluate if this Square instance is in the last row
    val onLastRow get() = r.index == 0
    // Declare an object to store the only avalaible instances this class can have.
    // This object is created once and every instance of this class has access to it,
    // but they cannot alter it since the constructor is private
    companion object {
        // If MAX_SQUARES == 4, values = [Square(2, a), Square(2, b), Square(1, a), Square(1, b)]
        val values = List(MAX_SQUARES) { idx ->
            Square((idx / BOARD_DIM).indexToRow(), (idx % BOARD_DIM).indexToColumn()) }
        // Since this class constructor is private, meaning we don't want to let the user
        // create more squares besides the already existing unique ones, it was considered useful
        // to have a public way to call the class as if the constructor wasn't private, although
        // invoke function can be used for other applications besides that
        operator fun invoke(rowIndex: Int, colIndex: Int) = values[rowIndex * BOARD_DIM + colIndex]
        operator fun invoke(r: Row, c: Column) = values[r.index * BOARD_DIM + c.index]
    }
    // Example: Square(2, a) = "2a"
    override fun toString() = "${row.number}${column.symbol}"
}

// Extension functions
fun String.toSquareOrNull() = Square.values.firstOrNull { sqr -> sqr.toString() == this }

/**
 * Evaluates if the [sqr] is in the same slash (sum of index of both row and column is equal)
 * of the square referenced by this
 **/
fun Square.onTheSameSlashOf(sqr: Square): Boolean =
    this.row.index + this.column.index == sqr.row.index + sqr.column.index