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

/**
 * Defines the dialog window that appears when an error occurs while connecting to the
 * online database. It uses the [AlertDialog] to achieve its functionality.
 * @param onDismiss Callback function to be executed when the dismiss button is clicked.
 */
@Composable
fun NoInternetDialog(onDismiss: () -> Unit) = AlertDialog(
    title = "Lost internet connection",
    icon = painterResource(BaseIcons.NoInternet),
    image = { Image(
        painterResource(BaseImages.NoInternet),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.size(150.dp).clip(CircleShape)
    )},
    centeredText = "No Internet",
    subtitleText = "No internet connection found. Check your connection or try again.",
    onDismiss = onDismiss
)