package checkers.ui.compose.windows.initial

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import checkers.ui.compose.base.BaseImages

/**
 * Represents the left panel of the initial window.
 */
@Composable
fun LeftPanel() =
    Image(
        painterResource(BaseImages.BoardWithCheckers),
        modifier = Modifier
            .requiredSize(
                width = 400.dp,
                height = INITIAL_WINDOW_HEIGHT
            )
            .border(Dp.Hairline, Color.Black),
        contentDescription = null
    )
