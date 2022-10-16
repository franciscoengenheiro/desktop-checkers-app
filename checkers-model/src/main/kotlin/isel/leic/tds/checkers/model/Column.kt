package isel.leic.tds.checkers.model

// Reminder: const val usage might cause problems when it's corresponding value is changed
// but all the files referencing it are not, resulting in unexpected errors
const val BOARD_DIM = 8

// @JvmInline associated with the below class (as a value class) cannot be used in this case,
// since there is a test in TestColumn.kt line 61, when comparing object instances, that fails
// The reason, according to Kotlinlang.org is: "Because inline classes may be represented both
// as the underlying value and as a wrapper, referential equality is pointless for them and is
// therefore prohibited".
class Column private constructor(val letter: Char) {
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
        // create more columns besides the already existing unique ones, it was considered useful
        // to have a public way to call the class as if the constructor wasn't private, although
        // invoke function can be used for other applications besides that
        operator fun invoke(letter: Char) = values.first { col -> letter == col.symbol }
    }
}

// Extension functions
fun Char.toColumnOrNull() = Column.values.firstOrNull { col -> this == col.symbol }
fun Int.indexToColumn() = Column.values[this]
// As mentioned in previous lectures, the function below is a good practice to have
// as it avoids calling an exception and instead returns null, useful when the objective
// isn't to stop the programm abruptly
// fun Int.indexToColumnOrNull() = if(this in Column.values.indices) indexToColumn() else null

