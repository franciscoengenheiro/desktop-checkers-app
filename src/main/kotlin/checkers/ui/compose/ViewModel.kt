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
import com.mongodb.MongoException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class ViewModel (private val scope: CoroutineScope) {
    var window by mutableStateOf<WindowState>(InitialWindow)
        private set
    var dialog by mutableStateOf<DialogState>(window.dialog)
        private set
    var error by mutableStateOf<String?>(null)
        private set
    lateinit var storage: BoardStorage
        // FileStorage("games", BoardSerializer)
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
    // Game state
    var game: Game? by mutableStateOf(null)
        private set
    // Game options state
    private var onRefresh by mutableStateOf(false)
    private var showTargets by mutableStateOf(true)
    private var autoRefresh by mutableStateOf(true)
    fun setBoardDimension(dim: BoardDim) {
        try {
            setGlobalBoardDimension(dim)
            storage = MongoDbAccess.createClient()
            window = MainWindow
        } catch (e: Exception) {
            when(e) {
                is MongoException -> openNoInternetDialog()
                else -> openErrorDialog(e.message)
            }
        }
    }
    fun newGame(name: String? = null) {
        if (name != null) {
            scope.launch {
                try {
                    game = createGame(name, storage)
                    closeDialog()
                    val g = game ?: return@launch
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
    fun resumeGame(name: String? = null, player: Player? = null) {
        if (name != null && player != null) {
            scope.launch {
                try {
                    game = resumeGame(name, player, storage)
                    closeDialog()
                    val g = game
                    g?.let { if (!isLocalPlayerTurn()) autoRefresh(it) }
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
            try {
                game = g.play(tosqr, fromSqr, storage, scope)
                scope.launch {
                    autoRefresh(g)
                }
            } catch (e: Exception) {
                when(e) {
                    is MongoException -> openNoInternetDialog()
                    else -> openErrorDialog(e.message)
                }
            }
        }
    }
    fun refresh() {
        val g = game ?: return
        scope.launch {
            val board: Board?
            try {
                board = storage.read(g.id)
                checkNotNull(board)
                game = g.copy(board = board)
            } catch (e: MongoException) {
                openNoInternetDialog()
            }
        }
    }
    private suspend fun autoRefresh(g: Game) {
        if (!autoRefreshStatus) return
        var board: Board? = null
        do {
            delay(1.seconds)
            try {
                board = storage.read(g.id)
                checkNotNull(board)
                game = g.copy(board = board)
            } catch (e: MongoException) {
                openNoInternetDialog()
            }
        } while (board is BoardRun && !isLocalPlayerTurn(board))
    }
    private fun isLocalPlayerTurn(board: Board? = null): Boolean {
        val g = game ?: return false
        val b = board ?: g.board
        return b is BoardRun && g.localPlayer == b.turn
    }
    fun showTargetsToggle() { showTargets = !showTargets }
    fun autoRefreshToggle() { autoRefresh = !autoRefresh }
    fun openNewGameDialog() { dialog = DialogState.NewGameDialog }
    fun openResumeGameDialog() { dialog = DialogState.ResumeGameDialog }
    fun openRulesDialog() { dialog = DialogState.RulesDialog }
    private fun openNoInternetDialog() { dialog = DialogState.NoInternetDialog }
    private fun openErrorDialog(msg: String?) {
        error = msg
        dialog = DialogState.OnError
    }
    fun closeDialog() { dialog = DialogState.NoDialogOpen }
}