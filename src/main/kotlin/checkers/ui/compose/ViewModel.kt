package checkers.ui.compose

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import checkers.model.Game
import checkers.model.board.*
import checkers.model.createGame
import checkers.model.moves.move.Player
import checkers.model.moves.move.Square
import checkers.model.play
import checkers.model.resumeGame
import checkers.storage.BoardStorage
import checkers.storage.MongoDbAccess
import checkers.ui.compose.dialogs.util.DialogState
import checkers.ui.compose.sound.MediaPlayer
import checkers.ui.compose.windows.InitialWindow
import checkers.ui.compose.windows.MainWindow
import checkers.ui.compose.windows.WindowState
import com.mongodb.MongoException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

private val REFRESH_DELAY = 1.seconds

/**
 * Represents the Application logic.
 * According to [Developer Android](https://developer.android.com/topic/libraries/architecture/viewmodel),
 * viewModel is a module that exposes state to the UI and encapsulates related business
 * logic. Its principal advantage is that it caches state and persists it
 * through configuration changes.
 */
class ViewModel (private val scope: CoroutineScope) {
    // Public states
    var window by mutableStateOf<WindowState>(InitialWindow)
        private set
    var dialog by mutableStateOf<DialogState>(window.dialog)
        private set
    var error by mutableStateOf<String?>(null)
        private set
    var game: Game? by mutableStateOf(null)
        private set
    // Private states
    private var onRefresh by mutableStateOf(false)
    private var showTargets by mutableStateOf(true)
    private var autoRefresh by mutableStateOf(true)
    private var soundEnabled by mutableStateOf(true)
    // Variable that represents the coroutine job of the auto refresh function.
    // By mantaining state of this job one can easily cancel it when necessary
    private var autoRefreshJob by mutableStateOf<Job?>(null)
    // Defines the storage where the game data will be stored
    lateinit var storage: BoardStorage
        // FileStorage("games", BoardSerializer)
    // Variables that represent game and option status:
    val refreshStatus: Boolean
        get() {
            val g = game ?: return false
            return !onRefresh && g.board is BoardRun && g.localPlayer != g.board.turn
        }
    val invertBoardStatus: Boolean
        get() {
            val g = game ?: return false
            return g.localPlayer == Player.b
        }
    val showTargetsStatus: Boolean
        get() = showTargets
    val autoRefreshStatus: Boolean
        get() = autoRefresh
    val soundStatus: Boolean
        get() = soundEnabled
    val gameFinishedStatus: Boolean
        get() {
            val g = game ?: return false
            return g.board !is BoardRun
        }
    // Functions:
    /**
     * Redirects the user to main window of the App, while setting the board global
     * dimension, connecting to the database and loading sounds. If an error occurs,
     * a dialog window will pop up with the message.
     * @param dim dimension to set [Dimension] value with.
     */
    fun redirectToMainWindow(dim: BoardDim) {
        MediaPlayer.loadSounds()
        setGlobalBoardDimension(dim)
        try {
            storage = MongoDbAccess.createClient()
            window = MainWindow
        } catch (e: Exception) {
            when (e) {
                is MongoException -> openNoInternetDialog()
                else -> openErrorDialog(e.message)
            }
        }
    }

    /**
     * Function called when the user requested a new game creation.
     * If an error occurs, a dialog window will pop up with the message.
     * @param name Name of the game to create.
     */
    fun newGame(name: String?) {
        if (name != null) {
            scope.launch {
                try {
                    game = createGame(name, storage)
                    if (soundEnabled) MediaPlayer.onBoardStart()
                    closeCurrentDialog()
                    val g = game ?: return@launch
                    autoRefreshJob?.cancel()
                    if (!isLocalPlayerTurn()) autoRefresh(g)
                } catch (e: Exception) {
                    when(e) {
                        is MongoException -> openNoInternetDialog()
                        else -> openErrorDialog(e.message)
                    }
                }
            }
        }
    }

    /**
     * Function called when the user requested to resume a game.
     * If an error occurs, a dialog window will pop up with the message.
     * @param name Name of the game to create.
     * @param player Type of the player to resume the game with.
     */
    fun resumeGame(name: String?, player: Player?) {
        if (name != null && player != null) {
            scope.launch {
                try {
                    game = resumeGame(name, player, storage)
                    closeCurrentDialog()
                    autoRefreshJob?.cancel()
                    val g = game
                    g?.let {
                        if (!isLocalPlayerTurn()) autoRefresh(it)
                    }
                } catch (e: Exception) {
                    when(e) {
                        is MongoException -> openNoInternetDialog()
                        else -> openErrorDialog(e.message)
                    }
                }
            }
        }
    }

    /**
     * Function called when the user requested a play on the board.
     * If an error occurs, a dialog window will pop up with the message.
     * @param toSqr Square to move a checker to.
     * @param fromSqr Square to move a checker from.
     */
    fun play(toSqr: Square, fromSqr: Square) {
        val g = game ?: return
        if (isLocalPlayerTurn()) {
            scope.launch {
                try {
                    game = g.play(toSqr, fromSqr, storage)
                    if (soundEnabled) MediaPlayer.onCheckerMove()
                    autoRefresh(g)
                } catch (e: Exception) {
                    when(e) {
                        is MongoException -> openNoInternetDialog()
                        // This exception was added to try/catch block in order to not
                        // constantly bother the user with invalid play warnings
                        is IllegalArgumentException -> {}
                        else -> openErrorDialog(e.message)
                    }
                }
            }
        }
    }

    /**
     * Function called when the user requested a refresh from the game storage.
     * This function also executes a delay before processing and can only be
     * executed again when it finishes.
     * If an error occurs, a dialog window will pop up with the message.
     */
    fun refresh() {
        val g = game ?: return
        if (onRefresh) return
        onRefresh = true
        scope.launch {
            try {
                delay(REFRESH_DELAY)
                val board: Board? = storage.read(g.id)
                checkNotNull(board)
                game = g.copy(board = board)
                onRefresh = false
            } catch (e: Exception) {
                when(e) {
                    is MongoException -> openNoInternetDialog()
                    else -> openErrorDialog(e.message)
                }
            }
        }
    }

    /**
     * This function is executed only if the user enabled auto refresh
     * in the Options menu. After a certain delay between searches, it constantly
     * checks if the other player has made a move and updates the board when it does.
     * If an error occurs, a dialog window will pop up with the message.
     */
    private suspend fun autoRefresh(g: Game) {
        if (!autoRefresh) return
        autoRefreshJob = scope.launch {
            do {
                delay(REFRESH_DELAY)
                var board: Board? = null
                try {
                    board = storage.read(g.id)
                    checkNotNull(board)
                    game = g.copy(board = board)
                } catch (e: Exception) {
                    when(e) {
                        is MongoException -> openNoInternetDialog()
                        else -> openErrorDialog(e.message)
                    }
                }
            } while (board is BoardRun && !isLocalPlayerTurn(board) && autoRefreshStatus)
            if (soundEnabled && !gameFinishedStatus) MediaPlayer.onCheckerMove()
        }
    }

    /**
     * Evaluates if the current turn of the board belongs to the local player.
     * @param board [Board] to evaluate. Is set to null by default.
     */
    private fun isLocalPlayerTurn(board: Board? = null): Boolean {
        val g = game ?: return false
        val b = board ?: g.board
        return b is BoardRun && g.localPlayer == b.turn
    }

    /**
     * Evaluates if the current [Board] type is at an end game state.
     * If it does, plays the respective sound and opens the end game dialog.
     */
    fun evaluateGameState() {
        val g = game ?: return
        when(g.board) {
            is BoardRun -> return
            is BoardDraw -> if (soundEnabled) MediaPlayer.onDraw()
            is BoardWin -> if (soundEnabled)
                                if (g.localPlayer == g.board.winner) MediaPlayer.onVictory()
                                else MediaPlayer.onDefeat()
        }
        openEndGameDialog()
    }
    // User driven actions - Toggle
    fun showTargetsToggle() { showTargets = !showTargets }
    fun autoRefreshToggle() {
        autoRefreshJob?.cancel()
        autoRefresh = !autoRefresh
    }
    fun soundToggle() { soundEnabled = !soundEnabled }
    // User driven actions - Dialog
    fun openNewGameDialog() { dialog = DialogState.NewGame }
    fun openResumeGameDialog() { dialog = DialogState.ResumeGame }
    fun openRulesDialog() { dialog = DialogState.Rules }
    // Internal reactions (not user related)
    private fun openNoInternetDialog() { dialog = DialogState.NoInternet }
    private fun openEndGameDialog() { dialog = DialogState.EndGame }
    private fun openErrorDialog(msg: String?) {
        error = msg
        dialog = DialogState.OnError
    }
    // Resets dialog state
    fun closeCurrentDialog() { dialog = DialogState.NoDialogOpen }
}