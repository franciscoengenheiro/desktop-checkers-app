package isel.leic.tds.checkers.ui.compose

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.Key.*
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import isel.leic.tds.checkers.ui.compose.base.BaseIcons

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FrameWindowScope.Menu(viewModel: ViewModel, onExit: () -> Unit) {
    MenuBar {
        Menu("Game") {
            Item(
                text = "New",
                icon = painterResource(BaseIcons.NewGame),
                shortcut = KeyShortcut(Key.N),
                mnemonic = 'N'
            ) { viewModel.newGame() }
            Item(
                text = "Resume",
                icon = painterResource(BaseIcons.ResumeGame),
                shortcut = KeyShortcut(Key.R),
                mnemonic = 'R'
            ) { viewModel.resume() }
            Item(
                text = "Refresh   ",
                icon = painterResource(BaseIcons.Refresh),
                shortcut = KeyShortcut(Key.E),
                mnemonic = 'e',
                onClick = viewModel::refresh,
                enabled = viewModel.refreshStatus
            )
            Item(
                text = "Exit",
                icon = painterResource(BaseIcons.Exit),
                shortcut = KeyShortcut(Key.Escape),
                onClick = onExit
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
            ) { viewModel.enableAutoRefreshToggle() }
        }
        Menu("Help") {
            Item(
                text = "Rules",
                icon = painterResource(BaseIcons.Help),
                shortcut = KeyShortcut(Key.U),
                mnemonic = 'u'
            ) { viewModel.showRulesToggle() }
        }
    }
}