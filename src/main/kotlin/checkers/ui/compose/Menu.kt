package checkers.ui.compose

import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.Key.*
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.*
import checkers.ui.compose.base.BaseIcons

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FrameWindowScope.Menu(viewModel: ViewModel, onExitRequest: () -> Unit) {
    MenuBar {
        Menu("Game") {
            Item(
                text = "New",
                icon = painterResource(BaseIcons.NewGame),
                shortcut = KeyShortcut(Key.N),
                mnemonic = 'N'
            ) { viewModel.openNewGameDialog() }
            Item(
                text = "Resume",
                icon = painterResource(BaseIcons.ResumeGame),
                shortcut = KeyShortcut(Key.R),
                mnemonic = 'R'
            ) { viewModel.openResumeGameDialog() }
            Item(
                text = "Refresh   ",
                icon = painterResource(BaseIcons.Refresh),
                shortcut = KeyShortcut(Key.E),
                mnemonic = 'e',
                onClick = viewModel::refresh,
                enabled = viewModel.refreshStatus
            )
            Separator()
            Item(
                text = "Exit",
                icon = painterResource(BaseIcons.Exit),
                shortcut = KeyShortcut(Key.Escape),
                onClick = onExitRequest
            )
        }
        Menu("Options") {
            CheckboxItem(
                text = "Show Targets",
                icon = painterResource(BaseIcons.ShowTargets),
                shortcut = KeyShortcut(Key.S),
                mnemonic = 'S',
                checked = viewModel.showTargetsStatus
            ) { viewModel.showTargetsToggle() }
            CheckboxItem(
                text = "Auto Refresh",
                icon = painterResource(BaseIcons.AutoRefresh),
                shortcut = KeyShortcut(Key.A),
                mnemonic = 'A',
                checked = viewModel.autoRefreshStatus
            ) { viewModel.autoRefreshToggle() }
        }
        Menu("Help") {
            Item(
                text = "Rules",
                icon = painterResource(BaseIcons.Help),
                shortcut = KeyShortcut(Key.U),
                mnemonic = 'u'
            ) { viewModel.openRulesDialog() }
        }
    }
}