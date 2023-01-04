package checkers.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.window.FrameWindowScope
import checkers.model.board.BoardRun
import checkers.ui.compose.board.BoardView
import checkers.ui.compose.dialogs.*
import checkers.ui.compose.dialogs.util.DialogState

@Composable
fun FrameWindowScope.App(viewModel: ViewModel, onExit: () -> Unit) {
    var dismissedEndGameDialog by remember { mutableStateOf(false) }
    Menu(viewModel, onExit)
    if (!dismissedEndGameDialog) viewModel.evaluateGameState()
    else if (viewModel.gameStatus) dismissedEndGameDialog = false
    when (viewModel.dialog) {
        DialogState.NewGame -> NewGameDialog(
            onConfirm = { name, _ -> viewModel.newGame(name) },
            onDismiss = { viewModel.closeCurrentDialog() }
        )
        DialogState.ResumeGame -> ResumeGameDialog(
            onConfirm = { name, player -> viewModel.resumeGame(name, player) },
            onDismiss = { viewModel.closeCurrentDialog() }
        )
        DialogState.Rules -> RulesDialog(
            onDismiss = { viewModel.closeCurrentDialog() }
        )
        DialogState.NoInternet -> NoInternetDialog(
            onDismiss = { viewModel.closeCurrentDialog() }
        )
        DialogState.OnError -> OnErrorDialog(
            message = viewModel.error,
            onDismiss = { viewModel.closeCurrentDialog() }
        )
        DialogState.EndGame -> viewModel.game?.let {
            EndGameDialog(
                game = it,
                onDismiss = {
                    dismissedEndGameDialog = true
                    viewModel.closeCurrentDialog()
                }
            )
        }
        DialogState.NoDialogOpen -> {}
    }
    Column {
        // Consult game state
        val game = viewModel.game
        BoardView(
            board = game?.board,
            invert = viewModel.invertBoardStatus.also { println(it) },
            localPlayer = game?.localPlayer,
            onPlay = viewModel::play,
            enableTargets = viewModel.showTargetsStatus
        )
        StatusBar(game)
    }
}