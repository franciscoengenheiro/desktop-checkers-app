package checkers.ui.compose.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import checkers.model.moves.move.Player
import checkers.ui.compose.dialogs.util.RadioButtonsInline
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

@Composable
private fun PlayerRadioButtons(): Player {
    Row(
        modifier = Modifier.absolutePadding(top = 10.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Choose a Player",
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold
        )
    }
    val options = PlayerRadioOptions
    val selectedOption = RadioButtonsInline(options.list)
    return options.getPlayer(selectedOption)
}

object PlayerRadioOptions {
    val list = listOf(Player.w.label(), Player.b.label())
    fun getPlayer(selectedOption: String): Player {
        return when (selectedOption) {
            Player.w.label() -> Player.w
            Player.b.label() -> Player.b
            else -> throw IllegalArgumentException(
                "Received string does not match a valid player"
            )
        }
    }
}