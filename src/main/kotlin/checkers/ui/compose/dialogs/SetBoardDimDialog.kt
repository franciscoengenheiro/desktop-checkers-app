package checkers.ui.compose.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState
import checkers.model.board.BoardDim
import checkers.ui.compose.base.BaseIcons
import checkers.ui.compose.dialogs.util.BoardDimButtons

@Composable
fun SetBoardDimDialog(
    onConfirm: (BoardDim) -> Unit,
    onDismiss: () -> Unit
) = Dialog(
    onCloseRequest = onDismiss,
    title = "title",
    state = DialogState(width = Dp.Unspecified, height = Dp.Unspecified),
    resizable = false,
    icon = painterResource(BaseIcons.WriteTextDialog)
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val boardDimension = BoardDimButtons()
        Button(onClick = { onConfirm(boardDimension) }) {
            Text("click")
        }
    }
}