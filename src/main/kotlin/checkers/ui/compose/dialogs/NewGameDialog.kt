package checkers.ui.compose.dialogs

import androidx.compose.runtime.Composable
import checkers.model.moves.move.Player
import checkers.ui.compose.dialogs.util.RequestInputDialog

/**
 * Defines the dialog window that appears when the user starts a new game.
 * It uses the [RequestInputDialog] to achieve its functionality.
 * @param onConfirm Callback function to be executed when the confirm button is clicked.
 * @param onDismiss Callback function to be executed when the dismiss button is clicked.
 */
@Composable
fun NewGameDialog(
    onConfirm: (String, Player) -> Unit,
    onDismiss: () -> Unit
) = RequestInputDialog(
    title = "New Game",
    confirmButtonText = "Create",
    content = { Player.w },
    onConfirm = onConfirm,
    onDismiss = onDismiss
)