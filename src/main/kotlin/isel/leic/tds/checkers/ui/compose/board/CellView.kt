package isel.leic.tds.checkers.ui.compose.board

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import isel.leic.tds.checkers.model.board.BOARD_DIM
import isel.leic.tds.checkers.model.moves.move.*
import isel.leic.tds.checkers.ui.compose.base.BaseColors
import isel.leic.tds.checkers.ui.compose.base.BaseImages

val CELL_VIEW_SIZE = 70.dp // The cell (box) size
private val SELECTED_CELL_BORDER_WIDTH = 3.dp

// Factors:
private const val CHECKER_VIEW_SCALE_FACTOR
    = 0.78f // Percentage of the cell which is covered by the square whose
            // sides equal the diameter of the checker image
private const val CIRCLE_VIEW_SCALE_FACTOR
    = 0.40f  // Percentage of the cell which is covered by the square whose
            // sides equal the diameter of the circle target

// Size calculations according to cell view size:
private val CHECKER_VIEW_SIZE
    = CELL_VIEW_SIZE * CHECKER_VIEW_SCALE_FACTOR  // The checker (box) image size
private val CIRCLE_VIEW_SIZE
    = CELL_VIEW_SIZE * CIRCLE_VIEW_SCALE_FACTOR // The target (box) circle size

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
    val defaultMod = Modifier
                        .size(CELL_VIEW_SIZE)
                        .background(colorRGB)
    Box (
        modifier =
            (if (selectedCell) defaultMod.border(SELECTED_CELL_BORDER_WIDTH, Color.Red)
            else defaultMod)
                .clickable(onClick = onClick)
                .border(Dp.Hairline, Color.DarkGray)

    ) {
        val color = if (sqr.black) BaseColors.LightBrown else BaseColors.DarkBrown
        val displayRow = sqr.column.index == 0 // Print row
        val displayColumn = sqr.row.index == BOARD_DIM - 1 // Print Column
        if (image == null) {
            if (drawTarget)
                Canvas(
                    modifier = Modifier
                       .size(CIRCLE_VIEW_SIZE)
                       .align(Alignment.Center), // This align method needs to be
                                                 // inside a BoxScope
                    onDraw = { drawCircle(color = BaseColors.LimeGreen) }
                )
        } else {
            Image(
                painterResource(image),
                contentScale = ContentScale.Crop,
                modifier = defaultMod
                    .requiredSize(CHECKER_VIEW_SIZE),
                    //.border(Dp.Hairline, Color.Black, CircleShape),
                contentDescription = null
            )
        }
        if (displayRow) {
            Box (modifier = Modifier.align(Alignment.TopStart)) {
                Text(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(horizontal = 2.dp, vertical = 2.dp),
                    text = "${sqr.row.number}",
                    fontSize = 15.sp,
                    color = color,
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        if (displayColumn) {
            Box (modifier = Modifier.align(Alignment.BottomEnd)) {
                Text(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(horizontal = 2.dp, vertical = 2.dp),
                    text = "${sqr.column.symbol}",
                    fontFamily = FontFamily.SansSerif,
                    color = color,
                    fontSize = 15.sp,
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}