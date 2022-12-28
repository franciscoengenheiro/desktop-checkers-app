package checkers.ui.compose.windows

import checkers.ui.compose.dialogs.util.DialogState

sealed class WindowState() {
    var dialog: DialogState = DialogState.NoDialogOpen
        set(value) {
            if (value !in this.dialogs)
                throw IllegalStateException("Dialog $value does not exist in this window state")
            else
                field = value
        }
    abstract val dialogs: List<DialogState>
    val title = "Desktop Checkers"
}

object InitialWindow: WindowState() {
    override val dialogs = listOf(
        DialogState.NoDialogOpen,
        DialogState.NoInternetDialog
    )
}

object MainWindow: WindowState() {
    override val dialogs = listOf(
        DialogState.NoDialogOpen,
        DialogState.NewGameDialog,
        DialogState.ResumeGameDialog,
        DialogState.RulesDialog,
        DialogState.NoInternetDialog
    )
}