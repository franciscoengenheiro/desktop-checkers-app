package checkers.model.moves.square

import checkers.model.board.BOARD_DIM

/**
 * Represents a column instance defined by a [letter].
 */
class Column private constructor(private val letter: Char) {
    // Class properties initialized with getters, so that their value is only calculated
    // when the property is called
    val index get() = letter.code - 'a'.code // Example: 0, 1, 2, ...
    val symbol get() = letter // Example: a, b, c, ...
    // Declare an object to store the only avalaible instances this class can have.
    // This object is created once and every instance of this class has access to it,
    // but they cannot alter it since the constructor is private
    companion object {
        val values = List(BOARD_DIM) { idx -> Column((idx + 'a'.code).toChar()) } // [a, b, c, ...]
        // Since this class constructor is private, meaning we don't want to let the user
        // create more columns besides the already existing unique ones, it was considered
        // useful to have a public way to call the class as if the constructor wasn't private
        operator fun invoke(letter: Char) = values.first { letter == it.symbol }
    }
    override fun toString() = "$letter"
}

// Extension functions:
/**
 * Evaluates if the given char is a match for a valid column.
 * @return The column it belongs to or null if none of the columns match.
 */
fun Char.toColumnOrNull() = Column.values.firstOrNull { col -> this == col.symbol }

/**
 * Evaluates if the given int is a match for a valid column index.
 * @return The column it belongs to.
 * @throws [IndexOutOfBoundsException] if the index does
 * not match any column.
 */
fun Int.indexToColumn() = Column.values[this]

// As mentioned in previous lectures, the function below is a good practice to have
// as it avoids calling an exception and instead returns null, useful when the objective
// isn't to stop the programm abruptly
/**
 * Evaluates if the given int is a match for a valid column index.
 * @return The column it belongs to or null if none of the columns match.
 */
fun Int.indexToColumnOrNull() = if(this in Column.values.indices) indexToColumn() else null