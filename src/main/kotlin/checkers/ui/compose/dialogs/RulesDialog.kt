package checkers.ui.compose.dialogs

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
import checkers.model.board.MAX_MOVES_WITHOUT_CAPTURE
import checkers.ui.compose.base.BaseIcons
import composables.ExpandableCard
import file.TextFile

@Composable
fun RulesDialog(onDismiss: ()->Unit) = Dialog(
    onCloseRequest = onDismiss,
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
                            "made by both players, without a capture ($MAX_MOVES_WITHOUT_CAPTURE), " +
                            "is reached.",
                    painter = painterResource(BaseIcons.Draw)
                )
            }
            items(Rule.list.size) { index ->
                ExpandableCard(
                    title = "Rule ${index + 1}",
                    description = Rule.list[index].description,
                    painter = painterResource(BaseIcons.Rule)
                )
            }
        }
    }
}

@JvmInline
value class Rule private constructor(val description: String) {
    companion object {
        private val rulesFromFile = TextFile.read("Rules")
        val list = List(rulesFromFile.size) { idx -> Rule(rulesFromFile[idx]) }
    }
}