package checkers.ui.compose.base

// Relative path src/main/resources directory
private const val relativePath = "icons/"

/**
 * Defines Application own icons.
 */
object BaseIcons {
    // App:
    const val App = "${relativePath}appIcon.png"
    // InitialWindow Screen
    const val GitHub = "${relativePath}gitHubIcon.png"
    // Dialogs:
    const val WriteTextDialog = "${relativePath}newGameDialogIcon.png"
    const val NoInternet = "${relativePath}noInternetIcon.png"
    const val AlertTriangle = "${relativePath}alertTriangleIcon.png"
    const val Info = "${relativePath}infoIcon.png"
    // Menu - Game:
    const val NewGame = "${relativePath}newGameIcon.png"
    const val ResumeGame = "${relativePath}resumeGameIcon.png"
    const val Refresh = "${relativePath}refreshIcon.png"
    const val Exit = "${relativePath}exitIcon.png"
    // Menu - Options:
    const val ShowTargets = "${relativePath}showTargetsIcon.png"
    const val AutoRefresh = "${relativePath}autoRefreshIcon.png"
    const val Sound = "${relativePath}soundIcon.png"
    // Menu - Help:
    const val Help = "${relativePath}helpIcon.png"
    const val Win = "${relativePath}winIcon.png"
    const val Draw = "${relativePath}drawIcon.png"
    const val Rule = "${relativePath}ruleIcon.png"
}