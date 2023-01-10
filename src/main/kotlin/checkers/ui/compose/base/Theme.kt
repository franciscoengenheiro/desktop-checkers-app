package checkers.ui.compose.base

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Material Theme used for the Application. Colors are set to [lightColors] and
 * some typography elements were overwritten.
 * @param content content where this material theme takes effect.
 */
@Composable
fun DesktopCheckersTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = lightColors(),
        typography = DesktopCheckersTypography,
        content = content
    )
}

private val DesktopCheckersTypography = Typography(
    defaultFontFamily = FontFamily.Default,
    h5 = TextStyle(
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 29.sp
    ),
    h6 = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 24.sp
    ),
    subtitle1 = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
    ),
    subtitle2 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.15.sp
    )
)