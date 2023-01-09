package checkers.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import checkers.model.Game
import checkers.model.board.BOARD_DIM
import checkers.model.board.BoardDraw
import checkers.model.board.BoardRun
import checkers.model.board.BoardWin
import checkers.ui.compose.board.BOARD_DIM_DIFF
import checkers.ui.compose.board.CELL_VIEW_SIZE

// Constants
private val GRID_OFFSET = if (-BOARD_DIM_DIFF <= 2) 2.dp else 1.dp
private val GRID_VIEW_SIZE = CELL_VIEW_SIZE * BOARD_DIM + GRID_OFFSET

@Composable
fun StatusBar(game: Game?) = Row(
    modifier = Modifier.background(Color.Black)
        .requiredWidth(GRID_VIEW_SIZE)
        .height(30.dp)
        .padding(horizontal = 2.dp),
    horizontalArrangement = Arrangement.SpaceBetween
) {
    val style: TextStyle = MaterialTheme.typography.h5
    if (game == null) {
        Text(
            text = "Start or resume a game",
            color = Color.White,
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
            color = Color.White,
            style = style
        )
        Text(
            text = "You: ${game.localPlayer.label()}",
            color = Color.White,
            style = style
        )
        Text(turnStatus, color = Color.White, style = style)
    }
}