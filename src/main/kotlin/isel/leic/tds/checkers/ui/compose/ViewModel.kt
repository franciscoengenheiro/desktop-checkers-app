package isel.leic.tds.checkers.ui.compose

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import isel.leic.tds.checkers.model.Game
import isel.leic.tds.checkers.model.board.Board
import isel.leic.tds.checkers.model.board.BoardRun
import isel.leic.tds.checkers.model.createGame
import isel.leic.tds.checkers.model.moves.move.Player
import isel.leic.tds.checkers.model.moves.move.Square
import isel.leic.tds.checkers.model.play
import isel.leic.tds.checkers.model.resumeGame
import isel.leic.tds.checkers.storage.BoardStorage
import isel.leic.tds.checkers.storage.MongoDbAccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class ViewModel(private val scope: CoroutineScope) {
    val storage: BoardStorage =
        // FileStorage("games", BoardSerializer)
        MongoDbAccess.createClient()
    val refreshStatus: Boolean
        get() {
            val g = game ?: return false
            return !onRefresh && g.board is BoardRun && g.localPlayer!=g.board.turn
        }
    val showTargetsStatus: Boolean
        get() = showTargets
    val autoRefreshStatus: Boolean
        get() = autoRefresh
    // Game state
    var game: Game? by mutableStateOf(null)
        private set
    // WriteTextDialog states
    var openNewGameDialog by mutableStateOf(false)
        private set
    var openResumeGameDialog by mutableStateOf(false)
        private set
    var openRulesDialog by mutableStateOf(false)
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
        openNewGameDialog = !openNewGameDialog
    }
    fun resume(name: String? = null, player: Player? = null) {
        if (name != null && player != null) {
            scope.launch {
                game = resumeGame(name, player, storage)
                val g = game
                g?.let {
                    if (!isPlayerTurn()) autoRefresh(it)
                }
            }
        }
        openResumeGameDialog = !openResumeGameDialog
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
    fun showRulesToggle() {
        openRulesDialog = !openRulesDialog
    }
    fun showTargetsToggle() {
        showTargets = !showTargets
    }
    fun enableAutoRefreshToggle() {
        autoRefresh = !autoRefresh
    }
}