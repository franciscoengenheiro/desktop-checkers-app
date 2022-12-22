package composables

import androidx.compose.desktop.ui.tooling.preview.Preview
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

@Preview
@Composable
private fun TestExpandableCard() {
    fun validateField(text: String): Boolean = text.length > 2
    OutlinedTextFieldWithValidation(
        validateField = ::validateField
    )
}