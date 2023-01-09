package checkers.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.FrameWindowScope
import checkers.ui.compose.board.BoardView
import checkers.ui.compose.dialogs.DialogController

@Composable
fun FrameWindowScope.App(viewModel: ViewModel, onExit: () -> Unit) {
    DialogController(viewModel)
    Menu(viewModel, onExit)
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