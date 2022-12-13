package isel.leic.tds.checkers.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.FrameWindowScope
import isel.leic.tds.checkers.ui.compose.board.BoardView
import isel.leic.tds.checkers.ui.compose.dialogs.NewGameDialog
import isel.leic.tds.checkers.ui.compose.dialogs.ResumeGameDialog
import isel.leic.tds.checkers.ui.compose.dialogs.RulesDialog

@Composable
fun FrameWindowScope.CheckersDesktopApp(onExit: () -> Unit) {
    // Returns a CoroutineScope in the current thread
    val scope = rememberCoroutineScope()
    val viewModel = remember{ ViewModel(scope) }
    Menu(viewModel, onExit)
    if (viewModel.openNewGameDialog) {
        NewGameDialog(
            onOk = { name -> viewModel.newGame(name) },
            onCancel = { viewModel.newGame() }
        )
    }
    if (viewModel.openResumeGameDialog) {
        ResumeGameDialog(
            onOk = { name, player -> viewModel.resume(name, player) },
            onCancel = { viewModel.resume() }
        )
    }
    if (viewModel.openRulesDialog) {
         RulesDialog(
             onCancel = { viewModel.showRulesToggle() }
         )
    }
    Column {
        BoardView(viewModel.game, onPlay = viewModel::play, viewModel.showTargetsStatus)
        StatusBar(viewModel.game)
    }
}