package checkers.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.FrameWindowScope
import checkers.ui.compose.board.BoardView
import checkers.ui.compose.dialogs.DialogState
import checkers.ui.compose.dialogs.NewGameDialog
import checkers.ui.compose.dialogs.ResumeGameDialog
import checkers.ui.compose.dialogs.RulesDialog
import checkers.ui.compose.dialogs.util.DialogState

@Composable
fun FrameWindowScope.App(viewModel: ViewModel, onExit: () -> Unit) {
    Menu(viewModel, onExit)
    when (viewModel.dialog) {
        DialogState.NewGameDialog -> NewGameDialog(
            onConfirm = { name, _ -> viewModel.newGame(name) },
            onDismiss = { viewModel.closeDialog() }
        )
        DialogState.ResumeGameDialog -> ResumeGameDialog(
            onConfirm = { name, player -> viewModel.resumeGame(name, player) },
            onDismiss = { viewModel.closeDialog() }
        )
        DialogState.RulesDialog -> RulesDialog(
            onDismiss = { viewModel.closeDialog() }
        )
        DialogState.NoDialogOpen -> {}
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

/*

SetBoardDimDialog(
    onConfirm = { _ -> viewModel.setBoardDimension(BoardDim.EIGHT) },
    onDismiss = { viewModel.setBoardDimension() }
)*/
