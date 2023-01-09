package checkers.ui.compose.sound

import pt.isel.canvas.loadSounds
import pt.isel.canvas.playSound

object MediaPlayer {
    fun loadSounds() {
        var sounds = emptyArray<String>()
        Sound.values().forEach { sounds += it.label }
        loadSounds(*sounds)
    }
    fun onVictory() = playSound(Sound.VICTORY.label)
    fun onDraw() = playSound(Sound.DRAW.label)
    fun onDefeat() = playSound(Sound.DEFEAT.label)
    fun onBoardStart() = playSound(Sound.BOARD_START.label)
    fun onCheckerMove() { playSound(checkerSounds().random()) }
    private fun checkerSounds() = Sound.values().mapNotNull {
        if (it.name.startsWith("CHECKER")) { it.label } else null
    }
}