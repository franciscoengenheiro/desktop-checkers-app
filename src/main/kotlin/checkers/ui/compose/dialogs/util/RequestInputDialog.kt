package checkers.ui.compose.dialogs.util

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState
import checkers.model.moves.move.Player
import checkers.ui.compose.base.BaseIcons
import composables.OutlinedTextFieldWithValidation

// Constants:
private const val MAX_GAME_ID_LENGTH = 10

@Composable
fun RequestInputDialog(
    title: String,
    confirmButtonText: String,
    content: @Composable (() -> Player),
    onConfirm: (gameName: String, selectedPlayer: Player) -> Unit,
    onDismiss: () -> Unit
) = Dialog(
    onCloseRequest = onDismiss,
    title = title,
    state = DialogState(width = 300.dp, height = Dp.Unspecified),
    resizable = false,
    icon = painterResource(BaseIcons.WriteTextDialog)
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val (name, invalidField) = OutlinedTextFieldWithValidation(::validateField)
        val selectedPlayer = content()
        AcknowledgeButtons(
            gameName = name,
            selectedPlayer = selectedPlayer,
            enable = !invalidField,
            confirmButtonText = confirmButtonText,
            onConfirm = onConfirm,
            onDismiss = onDismiss
        )
    }
}

private fun validateField(txt: String): Boolean {
    for(index in txt.indices) {
        if (!txt[index].isLetterOrDigit()) return false
    }
    return txt.length in 1.. MAX_GAME_ID_LENGTH
}