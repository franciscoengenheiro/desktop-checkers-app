package composables

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import checkers.ui.compose.base.BaseColors

@Composable
fun OutlinedTextFieldWithValidation(
    validateField: (text: String) -> Boolean
): Pair<String, Boolean> {
    var name by remember { mutableStateOf("") }
    var invalidField by remember { mutableStateOf(!validateField(name)) }
    OutlinedTextField(
        value = name,
        onValueChange = {
            name = it
            invalidField = !validateField(name)
        },
        label = { Text(
            text = "Input",
            color = Color.Black,
            style = MaterialTheme.typography.subtitle2
        ) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 10.dp),
        placeholder = { Text(
            text = "Enter game id",
            style = MaterialTheme.typography.subtitle2
        ) },
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

@Preview
@Composable
private fun TestOutlinedTextFieldWithValidation() {
    fun validateField(text: String): Boolean = text.length > 2
    OutlinedTextFieldWithValidation(
        validateField = ::validateField
    )
}