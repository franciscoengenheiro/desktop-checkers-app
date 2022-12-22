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
import checkers.model.board.BoardDim
import composables.radiobuttons.RadioButtons
import composables.radiobuttons.RadioOptions

@Composable
fun BoardDimButtons(): BoardDim {
    Row(
        modifier = Modifier.absolutePadding(top = 10.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Choose a Board Dimension",
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold
        )
    }
    val options = BoardDimRadioOptions
    lateinit var selectedOption: String
    Row {
        selectedOption = RadioButtons(options.list)
    }
    return options.get(selectedOption)
}

private object BoardDimRadioOptions: RadioOptions<BoardDim> {
    override fun set(): List<String> {
        var list = emptyList<String>()
        val dimensions = BoardDim.values()
        for (dim in dimensions) {
            list += "$dim"
        }
        return list
    }
    override fun get(selectedOption: String): BoardDim {
        return when (selectedOption) {
            "${BoardDim.EIGHT}" -> BoardDim.EIGHT
            "${BoardDim.TEN}" -> BoardDim.TEN
            "${BoardDim.TWELVE}" -> BoardDim.TWELVE
            else -> throw IllegalArgumentException(
                "Received string does not match a valid board dimension"
            )
        }
    }
}