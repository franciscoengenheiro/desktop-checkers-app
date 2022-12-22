package checkers.ui.compose.dialogs.util

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BoxWithText(
    boxModifier: Modifier,
    text: String,
    textColor: Color,
    textModifier: Modifier,
    fontSize: TextUnit
) = Box (modifier = boxModifier) {
    Text(
        modifier = textModifier,
        text = text,
        fontSize = fontSize,
        color = textColor,
        style = MaterialTheme.typography.subtitle1,
        fontWeight = FontWeight.Bold
    )
}

@Preview
@Composable
private fun BoxWithTextTest() {
    Box(
        modifier = Modifier.size(40.dp).background(Color.Green)
    ) {
        BoxWithText(
            boxModifier = Modifier
                .align(Alignment.TopStart)
                .background(Color.Yellow)
                .size(25.dp),
            text = "1",
            textColor = Color.Red,
            textModifier = Modifier.align(Alignment.Center),
            fontSize = 15.sp
        )
    }
}