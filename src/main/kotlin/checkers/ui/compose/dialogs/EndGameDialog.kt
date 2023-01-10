package checkers.ui.compose.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import checkers.model.Game
import checkers.model.board.BoardDraw
import checkers.model.board.BoardRun
import checkers.model.board.BoardWin
import checkers.ui.compose.base.BaseIcons
import checkers.ui.compose.dialogs.util.DrawState
import checkers.ui.compose.dialogs.util.LoseState
import checkers.ui.compose.dialogs.util.WinState
import composables.AlertDialog

/**
 * Defines an end game dialog that represents the received [game] state using a
 * generic [AlertDialog] composable.
 * @param game The current [game] instance.
 * @param onDismiss Callback function to be executed when the dismiss button is clicked.
 */
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

/**
 * Retrieves an end game state according to the received [game] instance.
 * @param game The current [game] instance.
 */
private fun getEndGameState(game: Game) =
    when (game.board) {
        is BoardWin -> if (game.board.winner == game.localPlayer)
            WinState else LoseState
        is BoardDraw -> DrawState
        is BoardRun -> throw IllegalStateException(
            "Cannot create an end game state on a ongoing Board" )
    }