package checkers.ui.compose.dialogs.util

import checkers.ui.compose.base.BaseImages

/**
 * Represents an end game state to be used in a composable.
 */
sealed interface EndGameState {
    val title: String
    val description: String
    val image: String
}

/**
 * Represents a state where the game has ended but the local player lost.
 */
object LoseState: EndGameState {
    override val title: String
        get() = "You lose"
    override val description: String
        get() = "You lost the game. Remember to control the center, " +
                "play agressive and be willing to sacrifice a checker when necessary. " +
                "Better luck next time!"
    override val image: String
        get() = BaseImages.Skull
}

/**
 * Represents a state where the game has ended but the local player won.
 */
object WinState: EndGameState {
    override val title: String
        get() = "You win"
    override val description: String
        get() = "Congratulations! Your victory established your status " +
                "as the world's foremost checkers player, but keep in mind " +
                "there are always more battles to be won!"
    override val image: String
        get() = BaseImages.Trophy
}

/**
 * Represents a state where the game has ended in a draw.
 */
object DrawState: EndGameState {
    override val title: String
        get() = "Game Tied"
    override val description: String
        get() = "You found your nemesis! The game ended in a draw because the maximum amount " +
                "of moves made without a capture was reached."
    override val image: String
        get() = BaseImages.Contract
}