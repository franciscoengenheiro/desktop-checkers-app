package checkers.model.board

/**
 * Defines dimension for the board.
 * @param value The actual integer value of a board dimension.
 */
enum class BoardDim(val value: Int) {
    EIGHT(8),
    TEN(10),
    TWELVE(12);
    override fun toString() = "${value}x${value}"
}

/**
 * Represents the global dimension of the board and can only be set to a [BoardDim] object.
 */
lateinit var Dimension: BoardDim

/**
 * Defines the global dimension of the board.
 * @param dim Dimension to set [Dimension].
 */
fun setGlobalBoardDimension(dim: BoardDim) { Dimension = dim }
/**
 * Defines the actual dimension for the board elements companion object constructors.
 * @throws UninitializedPropertyAccessException If [Dimension] is not yet initialized.
 */
fun setActualBoardDimension() =
    if (!(::Dimension.isInitialized))
        throw UninitializedPropertyAccessException(
            "Board global dimension hasn't been initialized")
    else
        Dimension.value