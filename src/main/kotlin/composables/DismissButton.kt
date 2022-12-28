package composables

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DismissButton(
    onButtonText: String,
    enable: Boolean,
    onDismiss: () -> Unit
) {
    Button(
        onClick = onDismiss,
        enabled = enable,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color(0xFFB00020), // DarkRed
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(10.dp)
    ) { Text(onButtonText) }
}

@Preview
@Composable
private fun TestDismissButton() {
    DismissButton(
        onButtonText = "Is this a test?",
        enable = true,
        onDismiss = {}
    )
}