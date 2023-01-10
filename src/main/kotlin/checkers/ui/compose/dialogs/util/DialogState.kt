package checkers.ui.compose.dialogs.util

/**
 * Represents a dialog state for the Application.
 */
enum class DialogState {
    NoDialogOpen, // State where no dialog is open
    NewGame,
    ResumeGame,
    Rules,
    NoInternet,
    OnError,
    EndGame
}