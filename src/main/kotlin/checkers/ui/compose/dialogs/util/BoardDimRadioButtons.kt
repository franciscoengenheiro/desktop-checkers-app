package checkers.ui.compose.dialogs.util

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import checkers.model.board.BoardDim
import composables.radiobuttons.RadioButtons
import composables.radiobuttons.RadioOptions

@Composable
fun BoardDimButtons(): BoardDim {
    val options = BoardDimRadioOptions
    lateinit var selectedOption: String
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.absolutePadding(top = 10.dp),
        ) {
            Text(
                text = "Choose a board dimension",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h6
            )
        }
        Column {
            selectedOption = RadioButtons(options.list)
        }
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