package checkers.ui.compose

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import checkers.model.Game
import checkers.model.board.Board
import checkers.model.board.BoardDim
import checkers.model.board.BoardRun
import checkers.model.board.setGlobalBoardDimension
import checkers.model.createGame
import checkers.model.moves.move.Player
import checkers.model.moves.move.Square
import checkers.model.play
import checkers.model.resumeGame
import checkers.storage.BoardStorage
import checkers.storage.MongoDbAccess
import checkers.ui.compose.dialogs.util.DialogState
import checkers.ui.compose.windows.InitialWindow
import checkers.ui.compose.windows.MainWindow
import checkers.ui.compose.windows.WindowState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL
import kotlin.time.Duration.Companion.seconds

private const val serverToPing = "https://google.com"

class ViewModel (private val scope: CoroutineScope) {
    var window by mutableStateOf<WindowState>(InitialWindow)
        private set
    var dialog by mutableStateOf<DialogState>(window.dialog)
        private set
    lateinit var storage: BoardStorage
        // FileStorage("games", BoardSerializer)
    val refreshStatus: Boolean
        get() {
            val g = game ?: return false
            return !onRefresh && g.board is BoardRun && g.localPlayer != g.board.turn
        }
    val invertGameBoardStatus: Boolean
        get() {
            val g = game ?: return false
            return g.localPlayer == Player.b
        }
    val showTargetsStatus: Boolean
        get() = showTargets
    val autoRefreshStatus: Boolean
        get() = autoRefresh
    // Game state
    var game: Game? by mutableStateOf(null)
        private set
    // Game options state
    private var onRefresh by mutableStateOf(false)
    private var showTargets by mutableStateOf(true)
    private var autoRefresh by mutableStateOf(true)
    fun setBoardDimension(dim: BoardDim) {
        if (!hasInternetConnection()) return
        setGlobalBoardDimension(dim)
        storage = MongoDbAccess.createClient()
        window = MainWindow
    }
    fun newGame(name: String? = null) {
        if (name != null) {
            scope.launch {
                if (!hasInternetConnection()) return@launch
                game = createGame(name, storage)
                val g = game ?: return@launch
                if (!isLocalPlayerTurn()) autoRefresh(g)
            }
        }
        closeDialog()
    }
    fun resumeGame(name: String? = null, player: Player? = null) {
        if (name != null && player != null) {
            scope.launch {
                if (!hasInternetConnection()) return@launch
                game = resumeGame(name, player, storage)
                val g = game
                g?.let { if (!isLocalPlayerTurn()) autoRefresh(it) }
            }
        }
        closeDialog()
    }
    fun play(tosqr: Square, fromSqr: Square) {
        val g = game ?: return
        if (isLocalPlayerTurn()) {
            if (!hasInternetConnection()) return
            game = g.play(tosqr, fromSqr, storage, scope)
            scope.launch {
                autoRefresh(g)
            }
        }
    }
    fun refresh() {
        val g = game ?: return
        scope.launch {
            if (!hasInternetConnection()) return@launch
            val board = storage.read(g.id)
            checkNotNull(board)
            game = g.copy(board = board)
        }
    }
    private suspend fun autoRefresh(g: Game) {
        if (!autoRefreshStatus) return
        var board: Board?
        do {
            delay(1.seconds)
            board = storage.read(g.id)
            if (!hasInternetConnection()) return
        } while (board is BoardRun && !isLocalPlayerTurn(board))
        checkNotNull(board)
        game = g.copy(board = board)
    }
    private fun isLocalPlayerTurn(board: Board? = null): Boolean {
        val g = game ?: return false
        val b = board ?: g.board
        return b is BoardRun && g.localPlayer == b.turn
    }
    private fun hasInternetConnection(): Boolean {
        val url = URL(serverToPing)
        var connection: HttpURLConnection? = null
        var connected: Boolean
        try {
            connection = url.openConnection() as HttpURLConnection
            connection.connect()
            if (dialog == DialogState.NoInternetDialog) closeDialog()
            connected = true
        } catch (e: Exception) {
            openNoInternetDialog()
            connected = false
        } finally {
            connection?.disconnect()
        }
        return connected
    }
    fun showTargetsToggle() { showTargets = !showTargets }
    fun autoRefreshToggle() { autoRefresh = !autoRefresh }
    fun openNewGameDialog() { dialog = DialogState.NewGameDialog }
    fun openResumeGameDialog() { dialog = DialogState.ResumeGameDialog }
    fun openRulesDialog() { dialog = DialogState.RulesDialog }
    private fun openNoInternetDialog() { dialog = DialogState.NoInternetDialog }
    fun closeDialog() { dialog = DialogState.NoDialogOpen }
}