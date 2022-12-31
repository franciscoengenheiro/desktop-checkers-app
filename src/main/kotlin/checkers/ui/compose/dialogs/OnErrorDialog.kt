package checkers.ui.compose.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import checkers.ui.compose.base.BaseIcons
import checkers.ui.compose.base.BaseImages
import composables.AlertDialog

@Composable
fun OnErrorDialog(
    message: String?,
    onDismiss: () -> Unit
) = AlertDialog(
    title = "An error ocurred",
    icon = painterResource(BaseIcons.AlertTriangle),
    image = { Image(
        painterResource(BaseImages.AlertTriangle),
        contentDescription = null,
        modifier = Modifier.size(150.dp)
    )},
    centeredText = "Error",
    subtitleText = message,
    onDismiss = onDismiss
)