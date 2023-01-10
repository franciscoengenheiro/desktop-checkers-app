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

/**
 * Composes an outlined text field that includes a placeholder, text validation
 * and icons that change according to the current text value.
 * @param inputText The option text placed above the text field.
 * @param placeholderText The optional placeholder to be displayed when the text field is
 * in focus and the input text is empty.
 * @param validateField Function that knows the requirements to validate the text field.
 */
@Composable
fun OutlinedTextFieldWithValidation(
    inputText: String = "Input",
    placeholderText: String = "",
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
            text = inputText,
            color = Color.Black,
            style = MaterialTheme.typography.subtitle2
        ) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 10.dp),
        placeholder = { Text(
            text = placeholderText,
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
        inputText = "other input",
        placeholderText = "This is a placeholder",
        validateField = ::validateField
    )
}