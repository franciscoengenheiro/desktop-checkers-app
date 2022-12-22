package checkers.ui.compose.dialogs.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import checkers.model.moves.move.Player
import composables.radiobuttons.RadioButtons
import composables.radiobuttons.RadioOptions

@Composable
fun PlayerRadioButtons(): Player {
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
    lateinit var selectedOption: String
    Row {
        selectedOption = RadioButtons(options.list)
    }
    return options.get(selectedOption)
}

private object PlayerRadioOptions: RadioOptions<Player> {
    override fun set(): List<String> {
        var list = emptyList<String>()
        val players = Player.values()
        for (player in players) {
            list += player.label()
        }
        return list
    }
    override fun get(selectedOption: String): Player =
        when (selectedOption) {
            Player.w.label() -> Player.w
            Player.b.label() -> Player.b
            else -> throw IllegalArgumentException(
                "Received string does not match a valid player"
            )
        }
}