package checkers

import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.*
import checkers.ui.compose.ViewModel
import checkers.ui.compose.windows.WindowState.*
import checkers.ui.compose.windows.InitialWindow
import checkers.ui.compose.windows.MainWindow
import com.mongodb.MongoException

fun main() {
    try {
        application {
            // Returns a CoroutineScope in the current thread
            val scope = rememberCoroutineScope()
            val viewModel = remember{ ViewModel(scope) }
            when(viewModel.window) {
                Initial -> InitialWindow(viewModel)
                Main -> MainWindow(viewModel)
            }
        }
    } catch(ex: Exception) {
        if (ex is MongoException) {
            // TODO(Open dialog window - connect to internet)
        }
        println(ex.message)
    }
}