package isel.leic.tds.checkers.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import isel.leic.tds.checkers.model.*
import isel.leic.tds.checkers.ui.compose.board.CELL_VIEW_SIZE

private val GRID_OFFSET = 2.dp
private val GRID_VIEW_SIZE = (CELL_VIEW_SIZE * BOARD_DIM) + GRID_OFFSET

@Composable
fun StatusBar(game: Game?) = Row(
    Modifier.background(MaterialTheme.colors.onSurface).width(GRID_VIEW_SIZE),
    horizontalArrangement = Arrangement.SpaceBetween
) {
    val style: TextStyle = MaterialTheme.typography.h6
    if (game == null) {
        Text("Start a new game", color = MaterialTheme.colors.onPrimary, style = style)
    } else {
        val turnStatus = when(game.board) {
            is BoardWin -> "Winner: ${game.board.winner}"
            is BoardDraw -> "Game tied"
            is BoardRun -> if(game.localPlayer == game.board.turn) "Your turn" else "Waiting.."
        }
        Text("Game: ${game.id}", color = MaterialTheme.colors.onPrimary, style = style)
        val player = when(game.localPlayer) {
            Player.w -> "White"
            Player.b -> "Black"
        }
        Text("You: $player", color = MaterialTheme.colors.onPrimary, style = style)
        Text(turnStatus, color = MaterialTheme.colors.onPrimary, style = style)
    }
}