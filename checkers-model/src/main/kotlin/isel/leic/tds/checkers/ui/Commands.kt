package isel.leic.tds.checkers.ui

import isel.leic.tds.checkers.model.*
import isel.leic.tds.checkers.storage.Storage

class Command(
    val execute: (args: List<String>, g: Game?) -> Game?,
    val show: (g: Game?) -> Unit = { g -> g?.board?.print() },
    val argsSyntax: String = "",
)

fun getCommands(storage: Storage<String, Board>) = mapOf(
    "START" to Command(
        execute = { args, _ -> createGame(args[0], storage) },
        argsSyntax = "<gameName>"
    ),
    "GRID" to Command( execute = { _, g ->
        // A game is required to have been created to use this command
        checkNotNull(g) { "Game not created" }
        g
    } ),
    "PLAY" to Command(
        execute = { args, g ->
            require(args.size == 2) { "Missing positions" }
            // A game is required to have been created to use this command
            checkNotNull(g) { "Game not created" }
            // Assert if received squares are valid squares on the board
            val fromSqr = args[0].toSquareOrNull()
            requireNotNull(fromSqr) { "Illegal square $fromSqr" }
            val toSqr = args[1].toSquareOrNull()
            requireNotNull(toSqr) { "Illegal square $toSqr" }
            g.play(fromSqr, toSqr, storage) },
        argsSyntax = "<fromSquare> <toSquare>",
    ),
    "REFRESH" to Command( execute = { _, g ->
        // A game is required to have been created to use this command
        checkNotNull(g) { "Game not created" }
        // Read the current board registered in the file
        val b = storage.read(g.id)
        check(b != null)
        // Copy the updated board to the game
        g.copy(board = b)
    } ),
    /*"CONTINUE" to Command( TODO("continue, pun intended")
        execute = TODO(),
        argsSyntax = "<gameName> w|b",
    ),*/
    "EXIT" to Command(
        execute = { _,_ -> null },
        show = { println("Closing the game") }
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

class SyntaxError(msg: String): IllegalArgumentException(msg)
