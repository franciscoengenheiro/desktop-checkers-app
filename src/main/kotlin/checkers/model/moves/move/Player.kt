package checkers.model.moves.move

/**
 * Represents a player in the game.
 */
enum class Player {
    w, // identifies the player with white checkers
    b; // identifies the player with black checkers
    /**
     * Method to retrieve the other [Player].
     */
    fun other() = if (this == w) b else w
    /**
     * Method to retrieve the [Player] label (name).
     * @return The defined string representation of this player.
     */
    fun label() =
        when(this) {
            w -> "White"
            b -> "Black"
        }
}
