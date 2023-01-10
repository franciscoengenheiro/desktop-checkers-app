package checkers.ui.compose.windows.initial

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import checkers.ui.compose.ViewModel
import checkers.ui.compose.base.BaseColors
import checkers.ui.compose.base.BaseIcons
import checkers.ui.compose.base.BaseImages
import checkers.ui.compose.dialogs.util.BoardDimRadioButtons

private const val developerGithubProfile = "https://github.com/FranciscoEngenheiro"
private const val emailTo = "checkersdesktopapp@gmail.com"
private val subject = "Report an issue".replace(" ", "+")

/**
 * Represents the right panel of the initial window.
 * @param viewModel View model that defines the application logic.
 */
@Composable
fun RightPanel(viewModel: ViewModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .requiredSize(
                width = 200.dp,
                height = INITIAL_WINDOW_HEIGHT
            )
            .border(Dp.Hairline, Color.Black)
            .paint(
                painterResource(BaseImages.VerticalMarble),
                contentScale = ContentScale.FillBounds
            )
    ) {
        Text(
            modifier = Modifier.absolutePadding(top = 10.dp, bottom = 20.dp),
            text = "Welcome to Desktop Checkers",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h5,
        )
        val selectedDimension = BoardDimRadioButtons()
        Button(
            modifier = Modifier.absolutePadding(bottom = 120.dp),
            onClick = { viewModel.redirectToMainWindow(selectedDimension) },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = BaseColors.DarkBrown,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(10.dp)
        ) { Text("Confirm") }
        ContactDeveloper()
    }
}

/**
 * Defines the contact developer bottom segment in the right panel.
 */
@Composable
private fun ContactDeveloper() {
    Text(
        text = "Contact Developer",
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.h5,
    )
    val uriHandler = LocalUriHandler.current
    Row {
        IconButton(
            onClick = { uriHandler.openUri(developerGithubProfile) },
            modifier = Modifier.paint(
                painter = painterResource(BaseIcons.GitHub),
                colorFilter = ColorFilter.tint(BaseColors.DarkBrown)
            )
        ) {}
        IconButton(
            onClick = {
                uriHandler.openUri(
                    "https://mail.google.com/mail/?view=cm&to=$emailTo&su=$subject"
                )
            }
        ) {
            Icon(
                imageVector = Icons.Filled.Email,
                contentDescription = null,
                tint = BaseColors.DarkBrown
            )
        }
    }
}