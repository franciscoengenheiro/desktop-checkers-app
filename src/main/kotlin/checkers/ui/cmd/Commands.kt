package checkers.ui.cmd

import checkers.model.Game
import checkers.model.createGame
import checkers.model.moves.move.Player
import checkers.model.moves.move.toSquareOrNull
import checkers.model.play
import checkers.model.resumeGame
import checkers.storage.BoardStorage
import kotlinx.coroutines.runBlocking
import kotlin.system.exitProcess

/**
 * Defines a Command for the [Game].
 * @param execute Function to execute whatever the command was created for.
 * @param show Prints the current state of the board by default.
 * @param argsSyntax Represents the correct expected syntax of this command arguments.
 */
class Command(
    val execute: (args: List<String>, g: Game?) -> Game?,
    val show: (g: Game?) -> Unit = { g -> g?.board?.print(g.localPlayer) },
    val argsSyntax: String = "",
)

/**
 * Distinguish a user syntax mistake with improper arguments from a regular
 * [IllegalArgumentException].
 */
class SyntaxError(msg: String): IllegalArgumentException(msg)

/**
 * Creates all commands in the game and associates them with their respective
 * identifier.
 */
fun getCommands(storage: BoardStorage) = mapOf(
    "START" to Command(
        execute = { args, g ->
            if (args.size != 1) throw SyntaxError("Missing game name")
            // Provide a coroutine scope to the suspend function
            // and await its completion by blocking the current thread
            runBlocking {
                createGame(args.first(), storage)
            }
        },
        argsSyntax = "<gameName>"
    ),
    "GRID" to Command(
        execute = { _, g ->
            // A game is required to have been created to use this command
            checkNotNull(g) { "Game not created" }
            g
        }
    ),
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
            // Provide a coroutine scope to the suspend function
            // and await its completion by blocking the current thread
            runBlocking {
                g.play(fromSqr, toSqr, storage)
            }
        },
        argsSyntax = "<fromSquare> <toSquare>",
    ),
    "REFRESH" to Command(
        execute = { _, g ->
            // A game is required to have been created to use this command
            checkNotNull(g) { "Game not created" }
            // Reads the current board stored in the file
            val board = runBlocking {
                storage.read(g.id)
            }
            checkNotNull(board)
            // Copies the updated board to the game
            g.copy(board = board)
        }
    ),
    "RESUME" to Command(
        execute = { args, g ->
            if (args.size != 2) throw SyntaxError("Missing game name and player")
            val player = when(args.last()) {
                "w" -> Player.valueOf(args.last())
                "b" -> Player.valueOf(args.last())
                else -> throw SyntaxError("Invalid player name")
            }
            // Provide a coroutine scope to the suspend function
            // and await its completion by blocking the current thread
            runBlocking {
                resumeGame(args.first(), player, storage)
            }
        },
        argsSyntax = "<gameName> w|b",
    ),
    "EXIT" to Command(
        execute = { _,_ -> // _ represents unused parameters
            println("Closing the application...")
            // Terminates the process where the game is running
            exitProcess(0)
        }
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