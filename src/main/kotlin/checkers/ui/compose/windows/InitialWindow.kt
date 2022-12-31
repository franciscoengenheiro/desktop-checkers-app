package checkers.ui.compose.windows

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import checkers.ui.compose.ViewModel
import checkers.ui.compose.base.BaseColors
import checkers.ui.compose.base.BaseIcons
import checkers.ui.compose.base.BaseImages
import checkers.ui.compose.dialogs.NoInternetDialog
import checkers.ui.compose.dialogs.OnErrorDialog
import checkers.ui.compose.dialogs.util.BoardDimButtons
import checkers.ui.compose.dialogs.util.DialogState

private val INITIAL_WINDOW_HEIGHT = 600.dp
private const val emailTo = "franciscoengenheiro.195@gmail.com"
private val subject = "Report an issue".replace(" ", "+")

@Composable
fun ApplicationScope.InitialWindow(viewModel: ViewModel) = Window(
    onCloseRequest = ::exitApplication,
    title = InitialWindow.title,
    state = WindowState(
        position = WindowPosition(Alignment.Center),
        size = DpSize.Unspecified
    ),
    icon = painterResource(BaseIcons.App),
    resizable = false
) {
    when(viewModel.dialog) {
        DialogState.NoInternetDialog -> NoInternetDialog(
            onDismiss = { viewModel.closeDialog() }
        )
        DialogState.OnError -> OnErrorDialog(
            message = viewModel.error,
            onDismiss = { viewModel.closeDialog() }
        )
    }
    Row(
        modifier = Modifier.border(1.dp, Color.Black),
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            painterResource(BaseImages.BoardWithCheckers),
            modifier = Modifier
                .requiredSize(
                    width = 400.dp,
                    height = INITIAL_WINDOW_HEIGHT)
                .border(Dp.Hairline, Color.Black),
            contentDescription = null
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .requiredSize(
                    width = 200.dp,
                    height = INITIAL_WINDOW_HEIGHT)
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
            val selectedDimension = BoardDimButtons()
            Button(
                modifier = Modifier.absolutePadding(bottom = 120.dp),
                onClick = { viewModel.setBoardDimension(selectedDimension) },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = BaseColors.DarkBrown,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp)
            ) { Text("Confirm") }
            Text(
                text = "Contact Developer",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h5,
            )
            val uriHandler = LocalUriHandler.current
            Row {
                IconButton(
                    onClick = { uriHandler.openUri("https://github.com/FranciscoEngenheiro") },
                    modifier = Modifier.paint(
                        painter = painterResource(BaseIcons.GitHub),
                        colorFilter = ColorFilter.tint(BaseColors.DarkBrown)
                    )
                ) {}
                IconButton(
                    onClick = { uriHandler.openUri(
                        "https://mail.google.com/mail/?view=cm&to=$emailTo&su=$subject") }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Email,
                        contentDescription = null,
                        tint = BaseColors.DarkBrown
                    )
                }
            }
        }
    }
}