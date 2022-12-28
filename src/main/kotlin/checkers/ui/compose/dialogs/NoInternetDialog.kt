package checkers.ui.compose.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.WindowPosition
import checkers.ui.compose.base.BaseIcons
import checkers.ui.compose.base.BaseImages
import composables.DismissButton

@Composable
fun NoInternetDialog(onDismiss: ()->Unit) = Dialog(
    onCloseRequest = onDismiss,
    title = "Lost internet connection",
    state = DialogState(
        position = WindowPosition(Alignment.Center),
        width = 250.dp,
        height = 400.dp
    ),
    resizable = false,
    icon = painterResource(BaseIcons.NoInternet)
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.padding(vertical = 10.dp))
        Image(
            painterResource(BaseImages.NoInternet),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(150.dp).clip(CircleShape)
        )
        Spacer(modifier = Modifier.padding(vertical = 10.dp))
        Text(
            text = "No Internet",
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.padding(vertical = 5.dp))
        Text(
            text = "No internet connection found. Check your connection or try again.",
            style = MaterialTheme.typography.subtitle2,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.padding(vertical = 10.dp))
        DismissButton(
            onButtonText = "Dismiss",
            enable = true,
            onDismiss = onDismiss
        )
        Spacer(modifier = Modifier.padding(vertical = 5.dp))
    }
}