package checkers

import checkers.model.Game
import checkers.model.board.BoardDim
import checkers.model.board.Dimension
import checkers.storage.BoardSerializer
import checkers.storage.BoardStorage
import checkers.storage.MongoDbAccess
import checkers.ui.cmd.*
import storage.FileStorage

fun main() {
    beforeBoardDimSetMsg().also { readBoardDimension() }
    val storage = saveGameDataIn("MongoDb")
    val cmds = getCommands(storage)
    var game: Game? = null
    afterBoardDimSetMsg()
    while (true) {
        val (name, args) = readCommand()
        val cmd: Command? = cmds[name]
        if (cmd == null) println("Invalid command.")
        else try {
            game = cmd.execute(args, game)
            cmd.show(game)
        } catch (ex: Exception) {
            println(ex.message)
            if (ex is SyntaxError)
                println("Use: $name ${cmd.argsSyntax}")
        }
    }
}

/**
 * Saves game data in a specified storage.
 * Currently supported storages: File (Simple text file), MongoDb (Online Documental database).
 * @return The indicated storage.
 * @throws [IllegalArgumentException] If that storage it's not supported.
 */
private fun saveGameDataIn(s: String): BoardStorage {
    val folder = "games"
    return when (s.uppercase()) {
        "FILE" -> FileStorage(folder, BoardSerializer)
        "MONGODB" -> MongoDbAccess.createClient()
        else -> throw IllegalArgumentException("Specified data storage it's not supported")
    }
}

/**
 * Prints a message to the stdout before a board dimension was set.
 */
private fun beforeBoardDimSetMsg() {
    var dimensions = ""
    for (dim in BoardDim.values()) {
        dimensions += "$dim "
    }
    listOf(
        "[INIT]: Checkers in command line!",
        "[REQUEST]: Choose a board dimension: $dimensions"
    ).forEach { println(it) }
}

/**
 * Prints a message to the stdout after a board dimension was set.
 */
private fun afterBoardDimSetMsg() =
    listOf(
        "[INFO]: Board dimension was set to: $Dimension",
        "[INFO]: To see the avalaible commands type: help"
    ).forEach { println(it) }