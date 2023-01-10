package checkers.ui.compose.dialogs

import androidx.compose.runtime.*
import checkers.ui.compose.ViewModel
import checkers.ui.compose.dialogs.util.DialogState

/**
 * Defines which dialog to present to the user.
 * @param viewModel View model that defines the application logic.
 */
@Composable
fun DialogController(viewModel: ViewModel) {
    // This variable is used to hold state of when the user dismissed the
    // end game dialog, so it will not show up again and only in the subsequent games.
    var enableEndGameDialog by remember { mutableStateOf(true) }
    // If the dialog wasn't dismissed yet evaluate the current game state
    if (enableEndGameDialog) viewModel.evaluateGameState()
    // Or if the game hasn't been finished:
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
                    enableEndGameDialog = false // User dismissed the end game dialog
                    viewModel.closeCurrentDialog()
                }
            )
        }
        DialogState.NoDialogOpen -> {}
    }
}