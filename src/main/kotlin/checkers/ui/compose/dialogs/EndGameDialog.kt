package checkers.ui.compose.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.InspectableModifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import checkers.model.Game
import checkers.model.board.BoardDraw
import checkers.model.board.BoardRun
import checkers.model.board.BoardWin
import checkers.ui.compose.base.BaseIcons
import checkers.ui.compose.base.BaseImages
import composables.AlertDialog

@Composable
fun EndGameDialog(
    game: Game,
    onDismiss: () -> Unit
) {
    val state = getEndGameState(game)
    AlertDialog(
        title = "The game has ended",
        icon = painterResource(BaseIcons.Info),
        image = { Image(
            painterResource(state.image),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(150.dp).clip(CircleShape)
        )},
        centeredText = state.title,
        subtitleText = state.description,
        onDismiss = onDismiss
    )
}

sealed class EndGameState() {
    abstract val title: String
    abstract val description: String
    abstract val image: String
}

object LoseState: EndGameState() {
    override val title: String
        get() = "You lose"
    override val description: String
        get() = "You lost the game. Remember to control the center, " +
                "play agressive and be willing to sacrifice a checker when necessary. " +
                "Better luck next time!"
    override val image: String
        get() = BaseImages.Skull
}

object WinState: EndGameState() {
    override val title: String
        get() = "You win"
    override val description: String
        get() = "Congratulations! Your victory established your status " +
                "as the world's foremost checkers player, but keep in mind " +
                "there are always more battles to be won!"
    override val image: String
        get() = BaseImages.Trophy
}

object DrawState: EndGameState() {
    override val title: String
        get() = "Game Tied"
    override val description: String
        get() = "The game ended in a draw because the total amount of moves " +
                "made without a capture was reached."
    override val image: String
        get() = BaseImages.Contract
}

@Composable
private fun getEndGameState(game: Game) =
    when (game.board) {
        is BoardWin -> if (game.board.winner == game.localPlayer)
            WinState else LoseState
        is BoardDraw -> DrawState
        is BoardRun -> throw IllegalStateException(
            "Cannot create an end game state on a ongoing Board" )
    }