package checkers.model.board

enum class BoardDim(val value: Int) {
    EIGHT(8),
    TEN(10),
    TWELVE(12);
    override fun toString() = "${value}x${value}"
}

lateinit var Dimension: BoardDim

fun setGlobalBoardDimension(dim: BoardDim) { Dimension = dim }

fun setActualBoardDimension() =
    if (!(::Dimension.isInitialized))
        throw UninitializedPropertyAccessException(
            "Board global dimension hasn't been initialized")
    else
        Dimension.value