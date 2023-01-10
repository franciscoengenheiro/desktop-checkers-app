package checkers.ui.compose.dialogs

import androidx.compose.runtime.Composable
import checkers.model.moves.move.Player
import checkers.ui.compose.dialogs.util.PlayerRadioButtons
import checkers.ui.compose.dialogs.util.RequestInputDialog

/**
 * Defines the dialog window that appears when the user resumes a game.
 * It uses the [RequestInputDialog] to achieve its functionality.
 * @param onConfirm Callback function to be executed when the confirm button is clicked.
 * @param onDismiss Callback function to be executed when the dismiss button is clicked.
 */
@Composable
fun ResumeGameDialog(
    onConfirm: (String, Player) -> Unit,
    onDismiss: () -> Unit
) = RequestInputDialog(
    title = "Resume Game",
    confirmButtonText = "Resume",
    content = { PlayerRadioButtons() },
    onConfirm = onConfirm,
    onDismiss = onDismiss
)