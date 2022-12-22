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
import checkers.ui.compose.windows.WindowState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class ViewModel (private val scope: CoroutineScope) {
    var dialog by mutableStateOf(DialogState.NoDialogOpen)
        private set
    var window by mutableStateOf(WindowState.Initial)
        private set
    fun setBoardDimension(dim: BoardDim) {
        setGlobalBoardDimension(dim)
        scope.launch {
            println("started")
            storage = MongoDbAccess.createClient()
        }
        println("ended")
        window = WindowState.Main
    }
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
    fun newGame(name: String? = null) {
        if (name != null) {
            scope.launch {
                game = createGame(name, storage)
                val g = game ?: return@launch
                if (!isPlayerTurn()) autoRefresh(g)
            }
        }
        closeDialog()
    }
    fun resumeGame(name: String? = null, player: Player? = null) {
        if (name != null && player != null) {
            scope.launch {
                game = resumeGame(name, player, storage)
                val g = game
                g?.let {
                    if (!isPlayerTurn()) autoRefresh(it)
                }
            }
        }
        closeDialog()
    }
    fun play(tosqr: Square, fromSqr: Square) {
        val g = game ?: return
        if (isPlayerTurn()) {
            game = g.play(tosqr, fromSqr, storage, scope)
            scope.launch {
                autoRefresh(g)
            }
        }
    }
    fun refresh() {
        val g = game ?: return
        scope.launch {
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
        } while (board is BoardRun && !isPlayerTurn(board))
        checkNotNull(board)
        game = g.copy(board = board)
    }
    private fun isPlayerTurn(board: Board? = null): Boolean {
        val g = game ?: return false
        val b = board ?: g.board
        return b is BoardRun && g.localPlayer == b.turn
    }
    fun showTargetsToggle() { showTargets = !showTargets }
    fun autoRefreshToggle() { autoRefresh = !autoRefresh }
    fun openNewGameDialog() { dialog = DialogState.NewGameDialog }
    fun openResumeGameDialog() { dialog = DialogState.ResumeGameDialog }
    fun openRulesDialog() { dialog = DialogState.RulesDialog }
    fun closeDialog() { dialog = DialogState.NoDialogOpen }
}