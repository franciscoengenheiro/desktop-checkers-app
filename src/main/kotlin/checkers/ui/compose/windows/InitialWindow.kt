package checkers.ui.compose.windows

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import checkers.ui.compose.ViewModel
import checkers.ui.compose.base.BaseIcons
import checkers.ui.compose.dialogs.SetBoardDimDialog

@Composable
fun ApplicationScope.InitialWindow(viewModel: ViewModel) = Window(
    onCloseRequest = ::exitApplication,
    title = "Initial",
    state = WindowState(
        position = WindowPosition(Alignment.Center),
        size = DpSize.Unspecified
    ),
    icon = painterResource(BaseIcons.App),
    resizable = false
) {
    MaterialTheme(colors = lightColors()) {
        SetBoardDimDialog( // TODO("change to window")
            onConfirm = { boardDim -> viewModel.setBoardDimension(boardDim) },
            onDismiss = ::exitApplication
        )
    }
}