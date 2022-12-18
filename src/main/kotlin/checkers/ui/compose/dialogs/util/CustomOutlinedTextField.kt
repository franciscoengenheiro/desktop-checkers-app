package checkers.ui.compose.dialogs.util

import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import checkers.ui.compose.base.BaseColors

// Constants:
private const val MAX_GAME_ID_LENGTH = 10

@Composable
fun CustomOutlinedTextField(): Pair<String, Boolean> {
    var name by remember { mutableStateOf("") }
    var invalidField by remember { mutableStateOf(!validateField(name)) }
    OutlinedTextField(
        value = name,
        onValueChange = {
            name = it
            invalidField = !validateField(name)
        },
        label = { Text("Input", color = Color.Black) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 10.dp),
        placeholder = { Text("Enter game id") },
        singleLine = true,
        trailingIcon = {
            if (invalidField)
                Icon(
                    Icons.Filled.Lock, "Invalid Input",
                    tint = BaseColors.DarkRed
                )
            else
                Icon(Icons.Filled.Done, "Valid Input")
        },
        isError = invalidField,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = BaseColors.DarkGreen
        )
    )
    return name to invalidField
}

private fun validateField(txt: String) =
    txt.length in 1.. MAX_GAME_ID_LENGTH && txt.isNotBlank()