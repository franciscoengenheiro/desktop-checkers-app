package checkers.ui.compose.dialogs.util

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import checkers.model.moves.move.Player
import checkers.ui.compose.base.BaseColors

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
        Button(
            onClick = onDismiss,
            enabled = enable,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = BaseColors.DarkRed,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(10.dp)
        ) { Text("Cancel") }
    }
}