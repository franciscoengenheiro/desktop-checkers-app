package checkers.ui.compose.windows.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import checkers.ui.compose.App
import checkers.ui.compose.ViewModel
import checkers.ui.compose.base.BaseIcons

/**
 * Defines the application main window.
 * @param viewModel View model that defines the application logic.
 */
@Composable
fun ApplicationScope.MainWindow(viewModel: ViewModel) = Window(
    onCloseRequest = ::exitApplication,
    title = checkers.ui.compose.windows.MainWindow.title,
    state = WindowState(
        position = WindowPosition(Alignment.Center),
        size = DpSize.Unspecified
    ),
    icon = painterResource(BaseIcons.App),
    resizable = false
) {
    App(viewModel = viewModel, onExitRequest = ::exitApplication)
}