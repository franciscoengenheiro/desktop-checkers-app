package isel.leic.tds.checkers.ui.compose.dialogs

import androidx.compose.foundation.ScrollbarAdapter
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.WindowPosition
import isel.leic.tds.checkers.model.board.MAX_MOVES_WITHOUT_CAPTURE
import isel.leic.tds.checkers.ui.compose.base.BaseIcons

@Composable
fun RulesDialog(onCancel: ()->Unit) = Dialog(
    onCloseRequest = onCancel,
    title = "Rules",
    state = DialogState(
        position = WindowPosition(BiasAlignment(0.75f, 0f)),
        width = 350.dp,
        height = 600.dp
    ),
    resizable = false,
    icon = painterResource(BaseIcons.Help)
) {
    Row {
        val listState = rememberLazyListState()
        VerticalScrollbar(
            adapter = ScrollbarAdapter(scrollState = listState),
            modifier = Modifier.padding(horizontal = 2.dp)
        )
        LazyColumn(
            state = listState
        ) {
            item {
                ExpandableCard(
                    title = "Win",
                    description = "Win the game by either capturing all of your opponent's " +
                            "checkers or by blocking them.",
                    painter = painterResource(BaseIcons.Win)
                )
            }
            item {
                ExpandableCard(
                    title = "Draw",
                    description = "A game will finish in a draw if a maximum limit of moves, " +
                            "made by both players, without a capture ($MAX_MOVES_WITHOUT_CAPTURE) " +
                            "is reached.",
                    painter = painterResource(BaseIcons.Draw)
                )
            }
            items(rulesList.size) { index ->
                ExpandableCard(
                    title = "Rule ${index + 1}",
                    description = rulesList[index].description,
                    painter = painterResource(BaseIcons.Rules)
                )
            }
        }
    }
}

@JvmInline
value class Rule(val description: String)

val rulesList = listOf(
    Rule("A regular checker (Piece) can be upgraded to King if it reaches " +
            "the first row of the board on the opponent's side."),
    Rule("A Piece can only move diagonally in a forward direction, " +
            "(towards the opponent), whereas a King can move forwards and backwards."),
    Rule("A King can't jump over the same color checkers or the " +
            "opponent's checkers if they're in contiguous diagonal squares, " +
            "meanwhile it can jump over contiguous empty diagonal squares."),
    Rule("Only when a piece is in a close diagonal of an opponent's checker " +
            "and there is an empty black square in the same diagonal, then a capture " +
            "can be made."),
    Rule("The opponent's checker gets eliminated after a successful capture."),
    Rule("A Piece can perform a backwards capture."),
    Rule("The player who crowns a Piece (to a King) loses it's turn, " +
            "even if there are more captures to be made from that position."),
    Rule("The process of capturing with a King is identical to that of capturing " +
            "with a Piece, except it is not necessary for the start and finish squares, " +
            "in the same diagonal, to be near the opponent checker that will be jumped."),
    Rule("A King cannot jump it's own color checkers."),
    Rule("To continue with the game, a capture must be made if one is available. " +
            "The player may select which capture to make if there are more than one " +
            "available at the beginning of its turn.")
)