package checkers.ui.compose.dialogs

import androidx.compose.runtime.Composable
import checkers.model.moves.move.Player
import checkers.ui.compose.dialogs.util.PlayerRadioButtons
import checkers.ui.compose.dialogs.util.RequestInputDialog

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