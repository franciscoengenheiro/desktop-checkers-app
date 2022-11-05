package isel.leic.tds.checkers

import com.mongodb.ConnectionString
import com.mongodb.client.MongoDatabase
import isel.leic.tds.checkers.model.Game
import isel.leic.tds.checkers.model.MAX_MOVES_WITHOUT_CAPTURE
import isel.leic.tds.checkers.ui.*
import isel.leic.tds.checkers.storage.FileStorage
import isel.leic.tds.checkers.storage.MongoStorage
import org.litote.kmongo.KMongo

private const val collection = "games"
private const val connStr = "mongodb+srv://FranciscoLEICTDS32D:agwEI2MWzbYXnCJE@cluster0.b4vcgdl.mongodb.net/?retryWrites=true&w=majority"
private val db: MongoDatabase = KMongo
    .createClient(ConnectionString(connStr))
    .getDatabase("Checkers")

fun main() {
    var game: Game? = null
    // val cmds = getCommands(FileStorage("checkers-model/output", BoardSerializer))
    val cmds = getCommands(MongoStorage(collection, db, BoardSerializer))
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
 * Prints a message regarding the game rules and conditions to the stdout
 */
private fun printWelcomeMsg() {
    val separator = "*".repeat(100) // TODO ("revisit")
    listOf(
        separator,
        "[Program]: Checkers in command line!",
        "[Win]: Win the game by either capturing all of your opponent's",
        "       checkers or by blocking them",
        "[Draw]: A game will finish in a draw if a maximum limit of valid moves, made by both players,",
        "        without a capture ($MAX_MOVES_WITHOUT_CAPTURE) was reached",
        "[Rules]: (1) - All checkers can only move from and to a black square.",
        "         (2) - A Piece can only move diagonally in a forward direction",
        "               (towards the opponent), whereas a King can move forward and backward.",
        "         (3) - A regular checker (Piece) can be upgraded to King if it reaches the first,",
        "               row of the board on thhe opponent's side.",
        "         (4) - A capture can only occurr if a checker is in close proximity of another",
        "               checker belonging to the opponent. If there's an empty black square in the diagonal,",
        "               then a capture is avalaible and once the capture is completed, the opponent's checker",
        "               will be removed. All types of checkers can make foward and backward captures.",
        "         (5) - If a capture is avalaible one must make it, in order to proceed. If more than one",
        "               capture is avalaible at the same time, the player can decide which one to make.",
        "[Labels]: b - black Piece",
        "          B - black King",
        "          w - white Piece",
        "          W - white King",
        "[Commands]: To see the avalaible commands type: help",
        separator
    ).forEach { println(it) }
}