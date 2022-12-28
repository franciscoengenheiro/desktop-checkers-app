package composables.radiobuttons

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RadioButtons(
    radioOptions: List<String>,
): String {
    require(radioOptions.size >= 2) {
        "Radio options provided are less than two"
    }
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
    radioOptions.forEach { option ->
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = (option == selectedOption),
                onClick = { onOptionSelected(option) },
                colors = RadioButtonDefaults.colors(Color.Black)
            )
            Text(text = option, style = MaterialTheme.typography.subtitle1)
            // Useful when inside a Row Scope
            Spacer(modifier = Modifier.padding(horizontal = 10.dp))
        }
    }
    return selectedOption
}

@Preview
@Composable
private fun RadioButtonInAColumn() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val list = remember { mutableStateListOf("A", "B", "C") }
        RadioButtons(list) // ignore return result
    }
}

@Preview
@Composable
private fun RadioButtonInARow() {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val list = remember { mutableStateListOf("A", "B", "C") }
        RadioButtons(list) // ignore return result
    }
}