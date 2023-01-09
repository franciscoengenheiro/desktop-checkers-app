package checkers.ui.compose.dialogs

import androidx.compose.runtime.*
import checkers.ui.compose.ViewModel
import checkers.ui.compose.dialogs.util.DialogState

@Composable
fun DialogController(viewModel: ViewModel) {
    var enableEndGameDialog by remember { mutableStateOf(true) }
    if (enableEndGameDialog) viewModel.evaluateGameState()
    else if (!viewModel.gameFinishedStatus) enableEndGameDialog = true
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
                    enableEndGameDialog = false
                    viewModel.closeCurrentDialog()
                }
            )
        }
        DialogState.NoDialogOpen -> {}
    }
}