package checkers.ui.compose.board

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import checkers.model.board.BOARD_DIM
import checkers.model.moves.move.*
import checkers.ui.compose.base.BaseColors
import checkers.ui.compose.base.BaseImages
import checkers.ui.compose.dialogs.util.BoxWithText

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
    invertRowId: Boolean,
    selectedCell: Boolean,
    drawTarget: Boolean,
    onClick: () -> Unit
) {
    println("CellView Recomposed")
    val image = when(checker) {
        is Piece -> if (checker.player == Player.w) BaseImages.whitePiece
                    else BaseImages.blackPiece
        is King -> if (checker.player == Player.w) BaseImages.whiteKing
                   else BaseImages.blackKing
        null -> null
    }
    val checkerColors = CheckerColors
    val colorRGB = if (sqr.black) checkerColors.black else checkerColors.white
    val defaultMod = Modifier.size(CELL_VIEW_SIZE).background(colorRGB)
    // Invert checker colors
    checkerColors.invertColors()
    val color = if (sqr.black) checkerColors.black else checkerColors.white
    // Invert back checker colors
    checkerColors.invertColors()
    val displayRow = sqr.column.index == 0 // Print row
    val displayColumn = if (invertRowId) sqr.row.index == 0
    else sqr.row.index == BOARD_DIM - 1 // Print Column
    Box (
        modifier =
            (if (selectedCell) defaultMod.border(SELECTED_CELL_BORDER_WIDTH, Color.Red)
            else defaultMod)
                .clickable(onClick = onClick)
                .border(Dp.Hairline, Color.DarkGray)
    ) {
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
                modifier = defaultMod.requiredSize(CHECKER_VIEW_SIZE),
                contentDescription = null
            )
        }
        if (displayRow)
            BoxWithText(
                boxModifier = Modifier.align(Alignment.TopStart),
                textModifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(horizontal = 2.dp, vertical = 2.dp),
                text = "${sqr.row.number}",
                textColor = color
            )
        if (displayColumn)
            BoxWithText(
                boxModifier = Modifier.align(Alignment.BottomEnd),
                textModifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(horizontal = 2.dp),
                text = "${sqr.column.symbol}",
                textColor = color
            )
    }
}