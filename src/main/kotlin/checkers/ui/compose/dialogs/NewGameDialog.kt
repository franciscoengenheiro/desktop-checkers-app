package checkers.ui.compose.dialogs

import androidx.compose.runtime.Composable
import checkers.model.moves.move.Player
import checkers.ui.compose.dialogs.util.RequestInputDialog

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