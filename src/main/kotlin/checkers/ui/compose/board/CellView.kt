package checkers.ui.compose.board

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import checkers.model.board.BOARD_DIM
import checkers.model.moves.move.*
import checkers.ui.compose.base.BaseColors
import checkers.ui.compose.base.BaseImages
import composables.BoxWithText

// Primary scale factor calculation:
private const val REFERENCE_BOARD_DIMENSION_SIZE = 8 // Preferable board size
val BOARD_DIM_DIFF = REFERENCE_BOARD_DIMENSION_SIZE - BOARD_DIM
// Sets 10% decrease in cell view size for each dimension above (+2) reference
// and 10% increase for each dimension below (-2) that same reference
private val BOARD_DIM_SCALE_FACTOR = BOARD_DIM_DIFF * 0.05f
val CELL_VIEW_SIZE = 70.dp * (BOARD_DIM_SCALE_FACTOR + 1f) // The cell (box) size

// Scale factors:
private const val CHECKER_VIEW_SCALE_FACTOR
        = 0.75f // Percentage of the cell which is covered by the square whose
                // sides equal the diameter of the checker image
private const val CIRCLE_VIEW_SCALE_FACTOR
        = 0.40f // Percentage of the cell which is covered by the square whose
                // sides equal the diameter of the circle target

// Size calculations according to cell view size:
private val CHECKER_VIEW_SIZE
    = CELL_VIEW_SIZE * CHECKER_VIEW_SCALE_FACTOR // The checker (box) image size
private val CIRCLE_VIEW_SIZE
    = CELL_VIEW_SIZE * CIRCLE_VIEW_SCALE_FACTOR // The target (box) circle size

// Other calculations:
private val SELECTED_CELL_BORDER_WIDTH = 3.dp
private const val REFERENCE_COLUMN_ROW_ID_FONT_SIZE = 15 // In sp (textUnit)
// Calculate colum/row id font size according to the board dimension reference.
// When board dimension increases by two, the respective column/row id font size
// will also decrease by 2.sp
private val COLUMN_ROW_ID_FONT_SIZE =
    (REFERENCE_COLUMN_ROW_ID_FONT_SIZE + BOARD_DIM_DIFF).sp

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
        is Piece -> if (checker.player == Player.w) BaseImages.WhitePiece
                    else BaseImages.BlackPiece
        is King -> if (checker.player == Player.w) BaseImages.WhiteKing
                   else BaseImages.BlackKing
        null -> null
    }
    val checkerColors = CheckerColors
    val colorRGB = if (sqr.black) checkerColors.black else checkerColors.white
    val defaultMod = Modifier.requiredSize(CELL_VIEW_SIZE).background(colorRGB)
    // Invert checker colors
    val colorRGBInverted = if (sqr.black) checkerColors.white else checkerColors.black
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
                       .requiredSize(CIRCLE_VIEW_SIZE)
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
                boxModifier = Modifier
                    .align(Alignment.TopStart),
                text = "${sqr.row.number}",
                textColor = colorRGBInverted,
                textModifier = Modifier
                    .align(Alignment.TopStart)
                    .absolutePadding(left = 2.dp, top = 1.dp),
                fontSize = COLUMN_ROW_ID_FONT_SIZE
            )
        if (displayColumn)
            BoxWithText(
                boxModifier = Modifier
                    .align(Alignment.BottomEnd),
                text = "${sqr.column.symbol}",
                textColor = colorRGBInverted,
                textModifier = Modifier
                    .align(Alignment.BottomEnd)
                    .absolutePadding(right = 2.dp, bottom = 1.dp),
                fontSize = COLUMN_ROW_ID_FONT_SIZE
            )
    }
}