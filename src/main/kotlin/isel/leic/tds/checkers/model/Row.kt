package isel.leic.tds.checkers.model

@JvmInline // Specifies this class as an inline class for the JVM
value class Row private constructor(private val num: Int) {
    // Class properties initialized with getters, so that their value is only calculated
    // when the property is called
    val index get() = BOARD_DIM - num  // Example: 0, 1, 2, ...
    val number get() = num // Example: 1, 2, 3, ...
    // Declare an object to store the only avalaible instances this class can have.
    // This object is created once and every instance of this class has access to it,
    // but they cannot alter it since the constructor is private
    companion object {
        val values = List(BOARD_DIM) { idx -> Row(BOARD_DIM - idx) } // [... , 3, 2, 1]
    }
    override fun toString() = "$num"
}

// Extension functions:
/**
 * Evaluates if the given int is a match for a valid row.
 * @return The row it belongs to or null if none of the rows match.
 */
fun Int.toRowOrNull() = Row.values.firstOrNull { row -> this == row.number }

/**
 * Evaluates if the given int is a match for a valid row index.
 * @return The row it belongs to or [IndexOutOfBoundsException] if the index does
 * not match any row.
 */
fun Int.indexToRow() = Row.values[this]

// As mentioned in previous lectures, the function below is a good practice to have
// as it avoids calling an exception and instead returns null, useful when the objective
// isn't to stop the programm abruptly
/**
 * Evaluates if the given int is a match for a valid index row.
 * @return The row it belongs to or null if none of the rows match.
 */
fun Int.indexToRowOrNull() = if(this in Row.values.indices) indexToRow() else null