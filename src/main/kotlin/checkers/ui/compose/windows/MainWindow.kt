package checkers.ui.compose.windows

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

@Composable
fun ApplicationScope.MainWindow(viewModel: ViewModel) = Window(
    onCloseRequest = ::exitApplication,
    title = MainWindow.title,
    state = WindowState(
        position = WindowPosition(Alignment.Center),
        size = DpSize.Unspecified
    ),
    icon = painterResource(BaseIcons.App),
    resizable = false
) {
    println("MainWindow recomposition")
    App(viewModel = viewModel, onExit = ::exitApplication)
}

