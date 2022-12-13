package isel.leic.tds.checkers.model

/**
 * Represents the players of the game: [w] to identify the player with white checkers
 * and [b] for the player with black checkers.
 */
enum class Player { w, b; // ; marks the end of the constants
    /**
     * Method to easily retrieve the other player
     */
    fun other() = if (this == w) b else w
}