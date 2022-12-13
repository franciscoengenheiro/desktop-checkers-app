package isel.leic.tds.checkers.ui.compose.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState
import isel.leic.tds.checkers.ui.compose.base.BaseColors
import isel.leic.tds.checkers.ui.compose.base.BaseIcons

// Constants:
// NOTE: Despite being a constant, the max game id length cannot take any value
// Programmer must ensure the Status Bar has enough space when all the other arguments
// have their possible largest values (as string representation), before altering.
private const val MAX_GAME_ID_LENGTH = 10

@Composable
fun NewGameDialog(onOk: (String)->Unit, onCancel: ()->Unit) = Dialog(
    onCloseRequest = onCancel,
    title = "New Game",
    state = DialogState(width = 300.dp, height = Dp.Unspecified),
    resizable = false,
    icon = painterResource(BaseIcons.WriteTextDialog)
) {
    var name by remember { mutableStateOf("") }
    var invalidField by remember { mutableStateOf(!validateField(name)) }
    Column {
        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                invalidField = !validateField(name)
            },
            label = { Text("Input", color = Color.Black) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp),
            placeholder = { Text("Enter game id") },
            singleLine = true,
            trailingIcon = {
                if (invalidField)
                    Icon(Icons.Filled.Lock, "Invalid Input",
                        tint = BaseColors.DarkRed)
                else
                    Icon(Icons.Filled.Done, "Valid Input")
            },
            isError = invalidField,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = BaseColors.DarkGreen
            )
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 15.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Button(
                onClick = { onOk(name) },
                enabled = !invalidField,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Black,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp)
            ) { Text("Create") }
            Button(
                onClick = onCancel,
                enabled = !invalidField,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = BaseColors.DarkRed,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp)
            ) { Text("Cancel") }
        }
    }
}

private fun validateField(txt: String) =
    txt.length in 1.. MAX_GAME_ID_LENGTH && txt.isNotBlank()