package isel.leic.tds.checkers.ui

import isel.leic.tds.checkers.model.*
import isel.leic.tds.checkers.storage.Storage
import kotlin.system.exitProcess

class Command(
    val execute: (args: List<String>, g: Game?) -> Game?,
    val show: (g: Game?) -> Unit = { g -> g?.board?.print(g) },
    val argsSyntax: String = "",
)

class SyntaxError(msg: String): IllegalArgumentException(msg)

fun getCommands(storage: Storage<String, Board>) = mapOf(
    "START" to Command(
        execute = { args, g ->
            if (g != null) throw IllegalArgumentException("Game already started")
            if (args.size != 1) throw SyntaxError("Missing game name")
            createGame(args.first(), storage) },
        argsSyntax = "<gameName>"
    ),
    "GRID" to Command( execute = { _, g ->
        // A game is required to have been created to use this command
        checkNotNull(g) { "Game not created" }
        g
    } ),
    "PLAY" to Command(
        execute = { args, g ->
            // A game is required to have been created to use this command
            checkNotNull(g) { "Game not created" }
            // A play requires two squares
            if (args.size != 2) throw SyntaxError("Missing squares")
            // Assert if received squares are valid squares on the board
            val fromSqr = args.first().toSquareOrNull()
                ?: throw SyntaxError("Invalid square ${args.first()}")
            val toSqr = args.last().toSquareOrNull()
                ?: throw SyntaxError("Invalid square ${args.last()}")
            g.play(fromSqr, toSqr, storage) },
        argsSyntax = "<fromSquare> <toSquare>",
    ),
    "REFRESH" to Command( execute = { _, g ->
        // A game is required to have been created to use this command
        checkNotNull(g) { "Game not created" }
        // Reads the current board stored in the file
        val b = storage.read(g.id)
        checkNotNull(b)
        // Copy the updated board to the game
        g.copy(board = b)
    } ),
    "CONTINUE" to Command(
        execute = { args, g ->
            require(g == null) { "Game already started" }
            if (args.size != 2) throw SyntaxError("Missing game name and player")
            val player = when(args.last()) {
                "w" -> Player.valueOf(args.last())
                "b" -> Player.valueOf(args.last())
                else -> throw SyntaxError("Invalid player name")
            }
            resumeGame(args.first(), player, storage) },
        argsSyntax = "<gameName> w|b",
    ),
    "EXIT" to Command(
        execute = { _,_ ->
            println("Closing the game")
            exitProcess(0) }
    )
).also { map ->
    // Create help command
    val helpCommand = Command(
        // This command doesn't care about the state of the game
        execute = { _,g -> g },
        // Print all previously created commands and their respective expected argsSyntax
        show = { map.forEach { println("${it.key} ${it.value.argsSyntax}") } }
    )
    // Add new command
    return map + Pair("HELP", helpCommand)
}