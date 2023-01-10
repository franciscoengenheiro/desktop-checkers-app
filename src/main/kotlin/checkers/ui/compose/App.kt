package checkers.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.FrameWindowScope
import checkers.ui.compose.dialogs.DialogController
import checkers.ui.compose.windows.main.Menu
import checkers.ui.compose.windows.main.StatusBar
import checkers.ui.compose.windows.main.board.BoardView

/**
 * Defines the Application compose implementation.
 * @param viewModel View model that defines the application logic.
 * @param onExitRequest Callback function to be executed when the user wants to exit through
 * the application interface.
 */
@Composable
fun FrameWindowScope.App(viewModel: ViewModel, onExitRequest: () -> Unit) {
    DialogController(viewModel)
    Menu(viewModel, onExitRequest)
    Column {
        // Consult game state
        val game = viewModel.game
        BoardView(
            board = game?.board,
            invert = viewModel.invertBoardStatus,
            localPlayer = game?.localPlayer,
            onPlay = viewModel::play,
            enableTargets = viewModel.showTargetsStatus
        )
        StatusBar(game)
    }
}