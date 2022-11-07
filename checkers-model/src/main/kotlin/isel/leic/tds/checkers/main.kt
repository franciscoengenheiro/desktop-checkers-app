package isel.leic.tds.checkers

import com.mongodb.ConnectionString
import com.mongodb.client.MongoDatabase
import isel.leic.tds.checkers.model.Game
import isel.leic.tds.checkers.model.MAX_MOVES_WITHOUT_CAPTURE
import isel.leic.tds.checkers.ui.*
import isel.leic.tds.checkers.storage.*
import org.litote.kmongo.KMongo

fun main() {
    val cmds = getCommandsFrom("MongoDb")
    var game: Game? = null
    printWelcomeMsg()
    while( true ) {
        val (name, args) = readCommand()
        val cmd: Command? = cmds[name]
        if (cmd == null) println("Invalid command: $name")
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
 * Access the game avalaible commands and saves game data in a specified storage.
 * Currently supported storages: File (Simple text file), MongoDb (Online Documental database).
 * @return All avalaible commands or throws [IllegalArgumentException] if the specified
 * storage is not supported.
 */
private fun getCommandsFrom(s: String) =
    when (s.uppercase()) {
        "FILE" -> getCommands(FileStorage("checkers-model/output", BoardSerializer))
        "MONGODB" -> {
            val collection = "games"
            val connStr = "mongodb+srv://FranciscoLEICTDS32D:agwEI2MWzbYXnCJE@cluster0.b4vcgdl.mongodb.net/?retryWrites=true&w=majority"
            val db: MongoDatabase = KMongo
                .createClient(ConnectionString(connStr))
                .getDatabase("Checkers")
            getCommands(MongoStorage(collection, db, BoardSerializer))
        }
        else -> throw IllegalArgumentException("Specified data storage it's not supported")
}

/**
 * Prints a message regarding the game rules and conditions to the stdout.
 */
private fun printWelcomeMsg() {
    val separator = "*".repeat(110)
    listOf(
        separator,
        "[Program]: CheckersCMD",
        "[Win]: Win the game by either capturing all of your opponent's",
        "       checkers or by blocking them.",
        "[Draw]: A game will finish in a draw if a maximum limit of moves, made by both players,",
        "        without a capture ($MAX_MOVES_WITHOUT_CAPTURE) is reached.",
        "[Rules]: (1) - In a game, the first player to enter will use the white checkers, while",
        "               the second player will use the black checkers.",
        "         (2) - A regular checker (Piece) can be upgraded to King if it reaches the first,",
        "               row of the board on the opponent's side.",
        "         (3) - The player who crowns a Piece (to a King) loses it's turn",
        "         (4) - All checkers can only move from and to a black square.",
        "         (5) - A Piece can only move diagonally in a forward direction (towards the opponent),",
        "               whereas a King can move forwards and backwards in a diagonal direction.",
        "         (6) - A King can't jump over the same color checkers or the opponent's checkers ",
        "               if they're in contiguous diagonal squares, meanwhile it can jump over ",
        "               contiguous empty diagonal squares.",
        "         (7) - Only when a piece is close to an opponent's checker and there is an empty",
        "               black square in the same diagonal may it be captured with a piece. The ",
        "               enemy's checker gets eliminated after a successful capture",
        "         (8) - A Piece can perform a backwards capture.",
        "         (9) - The process of capturing with a King is identical to that of capturing",
        "               with a Piece, except it is not necessary for the start and finish squares",
        "               to be near to the opponent checker that will be jumped.",
        "         (8) - If a capture is available, it must be made in order to continue. If more ",
        "               than one capture is possible at once, the player can choose which one to make.",
        "[Labels]: b - black Piece",
        "          B - black King",
        "          w - white Piece",
        "          W - white King",
        "[Commands]: To see the avalaible commands type: help",
        separator
    ).forEach { println(it) }
}