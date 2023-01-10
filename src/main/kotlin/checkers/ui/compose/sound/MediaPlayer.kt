package checkers.ui.compose.sound

import pt.isel.canvas.loadSounds
import pt.isel.canvas.playSound

/**
 * Represents the sound access. It defines functions to load and play
 * all the Application sounds.
 */
object MediaPlayer {
    // Loads all sounds from the App's library
    fun loadSounds() {
        var sounds = emptyArray<String>()
        Sound.values().forEach { sounds += it.label }
        loadSounds(*sounds)
    }
    // Functions that play sound:
    fun onVictory() = playSound(Sound.VICTORY.label)
    fun onDraw() = playSound(Sound.DRAW.label)
    fun onDefeat() = playSound(Sound.DEFEAT.label)
    fun onBoardStart() = playSound(Sound.BOARD_START.label)
    // For the user to not listen the same generic sound on a valid play, a list
    // of sounds were used to randomly select a sound in order to provide a
    // better user experience
    fun onCheckerMove() { playSound(checkerSounds.random()) }
    private val checkerSounds = Sound.values().mapNotNull {
        if (it.name.startsWith("CHECKER")) { it.label } else null
    }
}