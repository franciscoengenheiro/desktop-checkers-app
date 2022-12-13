package isel.leic.tds.checkers

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.mongodb.MongoException
import isel.leic.tds.checkers.ui.compose.CheckersDesktopApp
import isel.leic.tds.checkers.ui.compose.base.BaseIcons

fun main(){
    try {
        application {
            Window(
                onCloseRequest = ::exitApplication,
                title = "Checkers",
                state = WindowState(
                    position = WindowPosition(Alignment.Center),
                    size = DpSize.Unspecified
                ),
                icon = painterResource(BaseIcons.App),
                resizable = false
            ) {
                MaterialTheme(colors = lightColors()) {
                    CheckersDesktopApp(onExit = ::exitApplication)
                }
            }
        }
    } catch(ex: Exception) {
        if (ex is MongoException) {
            // TODO(Open dialog window - connect to internet)
        }
    }
}