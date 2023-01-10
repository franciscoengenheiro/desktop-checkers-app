package checkers.ui.compose.windows.initial

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import checkers.ui.compose.ViewModel
import checkers.ui.compose.base.BaseIcons
import checkers.ui.compose.dialogs.DialogController

val INITIAL_WINDOW_HEIGHT = 600.dp

/**
 * Defines the application initial window.
 * @param viewModel View model that defines the application logic.
 */
@Composable
fun ApplicationScope.InitialWindow(viewModel: ViewModel) = Window(
    onCloseRequest = ::exitApplication,
    title = checkers.ui.compose.windows.InitialWindow.title,
    state = WindowState(
        position = WindowPosition(Alignment.Center),
        size = DpSize.Unspecified
    ),
    icon = painterResource(BaseIcons.App),
    resizable = false
) {
    DialogController(viewModel)
    Row(
        modifier = Modifier.border(1.dp, Color.Black),
        horizontalArrangement = Arrangement.Center
    ) {
        LeftPanel()
        RightPanel(viewModel)
    }
}