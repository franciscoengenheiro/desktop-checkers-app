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

/**
 * Defines a specific dialog window that requests user input. This composable is not
 * generic and should only be used for the functions that support it.
 * @param title Dialog title.
 * @param confirmButtonText Text to place on top of the confirm button.
 * @param content Content to place above the acknowledge buttons.
 * This content must return a [Player].
 * @param onConfirm Callback function to be executed when the confirm button is clicked.
 * @param onDismiss Callback function to be executed when the dismiss button is clicked.
 */
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
        val (name, invalidField) = OutlinedTextFieldWithValidation(
            placeholderText = "Enter game id",
            validateField = ::validateField
        )
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

/**
 * Validates if each char of the received string is either a letter or a digit
 * and the total string does not surpass the [MAX_GAME_ID_LENGTH].
 */
private fun validateField(txt: String): Boolean {
    for(index in txt.indices) {
        if (!txt[index].isLetterOrDigit()) return false
    }
    return txt.length in 1.. MAX_GAME_ID_LENGTH
}