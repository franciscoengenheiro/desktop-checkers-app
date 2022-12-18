package checkers.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.FrameWindowScope
import checkers.ui.compose.board.BoardView
import checkers.ui.compose.dialogs.NewGameDialog
import checkers.ui.compose.dialogs.ResumeGameDialog
import checkers.ui.compose.dialogs.RulesDialog

@Composable
fun FrameWindowScope.App(onExit: () -> Unit) {
    // Returns a CoroutineScope in the current thread
    val scope = rememberCoroutineScope()
    val viewModel = remember{ ViewModel(scope) }
    Menu(viewModel, onExit)
    if (viewModel.openNewGameDialog) {
        NewGameDialog(
            onConfirm = { name, _ -> viewModel.newGame(name) },
            onDismiss = { viewModel.newGame() }
        )
    }
    if (viewModel.openResumeGameDialog) {
        ResumeGameDialog(
            onConfirm = { name, player -> viewModel.resume(name, player) },
            onDismiss = { viewModel.resume() }
        )
    }
    if (viewModel.openRulesDialog) {
         RulesDialog(
             onDismiss = { viewModel.showRulesToggle() }
         )
    }
    Column {
        // Consult game state
        val game = viewModel.game
        BoardView(
            board = game?.board,
            invert = viewModel.invertGameBoardStatus,
            localPlayer = game?.localPlayer,
            onPlay = viewModel::play,
            enableTargets = viewModel.showTargetsStatus
        )
        StatusBar(game)
    }
}