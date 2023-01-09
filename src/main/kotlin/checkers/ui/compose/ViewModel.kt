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

class ViewModel (private val scope: CoroutineScope) {
    var window by mutableStateOf<WindowState>(InitialWindow)
        private set
    var dialog by mutableStateOf<DialogState>(window.dialog)
        private set
    var error by mutableStateOf<String?>(null)
        private set
    private var autoRefreshJob by mutableStateOf<Job?>(null)
    lateinit var storage: BoardStorage
        // FileStorage("games", BoardSerializer)
    val refreshStatus: Boolean
        get() {
            val g = game ?: return false
            return !onRefresh && g.board is BoardRun && g.localPlayer != g.board.turn
        }
    // Game status
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
    // Game state
    var game: Game? by mutableStateOf(null)
        private set
    // Game options state
    private var onRefresh by mutableStateOf(false)
    private var showTargets by mutableStateOf(true)
    private var autoRefresh by mutableStateOf(true)
    private var soundEnabled by mutableStateOf(true)
    fun setBoardDimension(dim: BoardDim) {
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
    fun play(tosqr: Square, fromSqr: Square) {
        val g = game ?: return
        if (isLocalPlayerTurn()) {
            scope.launch {
                try {
                    game = g.play(tosqr, fromSqr, storage)
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
    private fun isLocalPlayerTurn(board: Board? = null): Boolean {
        val g = game ?: return false
        val b = board ?: g.board
        return b is BoardRun && g.localPlayer == b.turn
    }
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
    // User driven actions
    fun showTargetsToggle() { showTargets = !showTargets }
    fun autoRefreshToggle() {
        autoRefreshJob?.cancel()
        autoRefresh = !autoRefresh
    }
    fun soundToggle() { soundEnabled = !soundEnabled }
    fun openNewGameDialog() { dialog = DialogState.NewGame }
    fun openResumeGameDialog() { dialog = DialogState.ResumeGame }
    fun openRulesDialog() { dialog = DialogState.Rules }
    // Internal reactions
    private fun openNoInternetDialog() { dialog = DialogState.NoInternet }
    private fun openEndGameDialog() { dialog = DialogState.EndGame }
    private fun openErrorDialog(msg: String?) {
        error = msg
        dialog = DialogState.OnError
    }
    // Independent
    fun closeCurrentDialog() { dialog = DialogState.NoDialogOpen }
}