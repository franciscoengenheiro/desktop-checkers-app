package isel.leic.tds.checkers.ui.compose.board

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import isel.leic.tds.checkers.model.*
import isel.leic.tds.checkers.ui.compose.base.BaseColors
import isel.leic.tds.checkers.ui.compose.base.BaseImages

val CELL_VIEW_SIZE = 50.dp
private val CIRCLE_VIEW_SIZE = 35.dp

@Composable
fun CellView(
    sqr: Square,
    checker: Checker?,
    selectedCell: Boolean,
    drawTarget: Boolean,
    onClick: () -> Unit
) {
    // println("CellView Recomposed")
    val image = when(checker) {
        is Piece -> if (checker.player == Player.w) BaseImages.whitePiece
        else BaseImages.blackPiece
        is King -> if (checker.player == Player.w) BaseImages.whiteKing
        else BaseImages.blackKing
        null -> null
    }
    val colorRGB = if (sqr.black) BaseColors.DarkBrown else BaseColors.LightBrown
    val defaultMod = Modifier.size(CELL_VIEW_SIZE).background(colorRGB)
    Box (
        modifier = defaultMod.clickable(onClick = onClick)
    ) {
        if (image == null) {
            if (drawTarget)
                Canvas(
                    modifier = Modifier
                        .size(CIRCLE_VIEW_SIZE)
                        .align(Alignment.Center), // This align method needs to be inside
                    // a scope, in this case a BoxScope
                    onDraw = { drawCircle(color = BaseColors.LimeGreen) }
                )
        } else {
            val mod = if (selectedCell) defaultMod.border(3.dp, Color.Red) else defaultMod
            Image(
                painterResource(image),
                modifier = mod,
                contentDescription = null
            )
        }
    }
}