package checkers

import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.application
import checkers.ui.compose.DesktopCheckersTheme
import checkers.ui.compose.ViewModel
import checkers.ui.compose.windows.InitialWindow
import checkers.ui.compose.windows.MainWindow

fun main() {
    application {
        // Returns a CoroutineScope in the current thread
        val scope = rememberCoroutineScope()
        val viewModel = remember { ViewModel(scope) }
        DesktopCheckersTheme {
            when (viewModel.window) {
                InitialWindow -> InitialWindow(viewModel)
                MainWindow -> MainWindow(viewModel)
            }
        }
    }
}
