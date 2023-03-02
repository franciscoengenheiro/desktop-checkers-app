package checkers.ui.compose.sound

/**
 * Defines a sound used in the Application.
 * @param label name of the sound file without the (.wav) extension
 */
enum class Sound(val label: String) {
    VICTORY("victory"),
    DEFEAT("defeat"),
    DRAW("drawing"),
    BOARD_START("boardStart"),
    CHECKER1("moveChecker1"),
    CHECKER2("moveChecker2"),
    CHECKER3("moveChecker3"),
    CHECKER4("moveChecker4"),
    CHECKER5("moveChecker5"),
    CHECKER6("moveChecker6")
}
