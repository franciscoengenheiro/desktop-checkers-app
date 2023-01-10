package checkers.ui.compose.windows

import checkers.ui.compose.dialogs.util.DialogState

/**
 * Represents a window state for the Application.
 */
sealed class WindowState {
    var dialog: DialogState = DialogState.NoDialogOpen
        set(value) {
            check(value !in this.dialogs) { "Dialog $value does not exist in $this" }
            field = value
        }
    abstract val dialogs: List<DialogState>
    val title = "Desktop Checkers"
}

/**
 * Represents the window state where the game is presented to the user.
 */
object InitialWindow: WindowState() {
    override val dialogs = listOf(
        DialogState.NoDialogOpen,
        DialogState.NoInternet,
        DialogState.OnError
    )
}

/**
 * Represents the window state where the game is playable.
 */
object MainWindow: WindowState() {
    override val dialogs = listOf(
        DialogState.NoDialogOpen,
        DialogState.NewGame,
        DialogState.ResumeGame,
        DialogState.Rules,
        DialogState.NoInternet,
        DialogState.OnError,
        DialogState.EndGame
    )
}