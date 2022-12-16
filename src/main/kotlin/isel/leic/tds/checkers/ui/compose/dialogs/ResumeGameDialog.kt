package isel.leic.tds.checkers.ui.compose.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState
import isel.leic.tds.checkers.model.moves.move.Player
import isel.leic.tds.checkers.ui.compose.base.BaseColors
import isel.leic.tds.checkers.ui.compose.base.BaseIcons

@Composable
fun ResumeGameDialog(onOk: (String, Player)->Unit, onCancel: ()->Unit) = Dialog(
    onCloseRequest = onCancel,
    title = "Resume Game",
    state = DialogState(width = 300.dp, height = Dp.Unspecified),
    resizable = false,
    icon = painterResource(BaseIcons.WriteTextDialog)
) {
    var name by remember { mutableStateOf("") }
    var invalidField by remember { mutableStateOf(!validateField(name)) }
    var selectedPlayer by remember { mutableStateOf(Player.w) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedPlayer == Player.w,
                    onClick = { selectedPlayer = Player.w },
                    colors = RadioButtonDefaults.colors(Color.Black)
                )
                Text(text = "White", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.padding(horizontal = 10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedPlayer == Player.b,
                    onClick = { selectedPlayer = Player.b },
                    colors = RadioButtonDefaults.colors(Color.Black)
                )
                Text(text = "Black", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.padding(horizontal = 10.dp))
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Button(
                onClick = { onOk(name, selectedPlayer) },
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

private fun validateField(txt: String) = txt.length in 1..10 && txt.isNotBlank()