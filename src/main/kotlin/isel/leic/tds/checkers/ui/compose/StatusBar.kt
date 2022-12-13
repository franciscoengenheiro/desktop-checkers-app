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
import androidx.compose.ui.text.style.TextOverflow
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
    val style: TextStyle = MaterialTheme.typography.h5
    if (game == null) {
        Text(
            text = "Start or resume a game",
            color = MaterialTheme.colors.onPrimary,
            style = style)
    } else {
        val turnStatus = when(game.board) {
            is BoardWin -> {
                "Winner: ${game.board.winner.label()}"
            }
            is BoardDraw -> "Game tied"
            is BoardRun -> if (game.localPlayer == game.board.turn) "Your turn"
                           else "Waiting.."
        }
        Text(
            text = "Game: ${game.id}",
            color = MaterialTheme.colors.onPrimary,
            style = style,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "You: ${game.localPlayer.label()}",
            color = MaterialTheme.colors.onPrimary,
            style = style
        )
        Text(turnStatus, color = MaterialTheme.colors.onPrimary, style = style)
    }
}