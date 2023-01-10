package checkers.ui.compose.dialogs.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import checkers.model.moves.move.Player
import composables.DismissButton

/**
 * Draws two bottons in a row representing the confirm and dismiss buttons respectively.
 * This composable is not generic and should only be used for the functions that support it.
 * @param gameName Game indentifier.
 * @param selectedPlayer Player selected to start the game.
 * @param enable Controls the enabled state of the buttons. When false, this buttons will
 * not be clickable.
 * @param confirmButtonText Text to place on top of the confirm button.
 * @param onConfirm Callback function to be executed when the confirm button is clicked.
 * @param onDismiss Callback function to be executed when the dismiss button is clicked.
 */
@Composable
fun AcknowledgeButtons(
    gameName: String,
    selectedPlayer: Player,
    enable: Boolean,
    confirmButtonText: String,
    onConfirm: (gameName: String, selectedPlayer: Player) -> Unit,
    onDismiss: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().absolutePadding(top = 5.dp, bottom = 15.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        Button(
            onClick = { onConfirm(gameName, selectedPlayer) },
            enabled = enable,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Black,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(10.dp)
        ) { Text(confirmButtonText) }
        DismissButton(
            onButtonText = "Cancel",
            enable = enable,
            onDismiss = onDismiss
        )
    }
}