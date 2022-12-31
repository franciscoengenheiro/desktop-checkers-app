package composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.WindowPosition

@Composable
fun AlertDialog(
    title: String,
    icon: Painter? = null,
    image: @Composable () -> Unit,
    centeredText: String,
    subtitleText: String?,
    onDismiss: () -> Unit
) = Dialog(
    onCloseRequest = onDismiss,
    title = title,
    state = DialogState(
        position = WindowPosition(Alignment.Center),
        width = 250.dp,
        height = Dp.Unspecified
    ),
    resizable = false,
    icon = icon
) {
    Column(
        modifier = Modifier.padding(horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.padding(vertical = 10.dp))
        image()
        Spacer(modifier = Modifier.padding(vertical = 5.dp))
        Text(
            text = centeredText,
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.padding(vertical = 5.dp))
        subtitleText?.let {
            Row(
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = subtitleText,
                    style = MaterialTheme.typography.subtitle2,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 3
                )
            }
        }
        Spacer(modifier = Modifier.padding(vertical = 10.dp))
        DismissButton(
            onButtonText = "Dismiss",
            enable = true,
            onDismiss = onDismiss
        )
        Spacer(modifier = Modifier.padding(vertical = 5.dp))
    }
}