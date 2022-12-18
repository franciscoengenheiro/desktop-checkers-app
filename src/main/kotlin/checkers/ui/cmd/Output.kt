package checkers.ui.cmd

import checkers.model.board.*
import checkers.model.moves.move.Player
import checkers.model.moves.move.Square
import checkers.model.moves.square.Column

/*
      Board print model, assuming BOARD_DIM == 8
   ╔═════╦═════╦═════╦═════╦═════╦═════╦═════╦═════╗
 8 ║     ║  b  ║     ║  b  ║     ║  b  ║     ║  b  ║  Turn = b
   ╠-----╬-----╬-----╬-----╬-----╬-----╬-----╬-----╣
 7 ║  b  ║     ║  b  ║     ║  b  ║     ║  b  ║     ║  Player = w
   ╠-----╬-----╬-----╬-----╬-----╬-----╬-----╬-----╣
 6 ║     ║  b  ║     ║  b  ║     ║  b  ║     ║  b  ║
   ╠-----╬-----╬-----╬-----╬-----╬-----╬-----╬-----╣
 5 ║  -  ║     ║  -  ║     ║  -  ║     ║  -  ║     ║
   ╠═════╬═════╠═════╬═════╠═════╬═════╠═════╬═════╣
 4 ║     ║  -  ║     ║  -  ║     ║  -  ║     ║  -  ║
   ╠-----╬-----╬-----╬-----╬-----╬-----╬-----╬-----╣
 3 ║  w  ║     ║  w  ║     ║  w  ║     ║  w  ║     ║
   ╠-----╬-----╬-----╬-----╬-----╬-----╬-----╬-----╣
 2 ║     ║  w  ║     ║  w  ║     ║  w  ║     ║  w  ║
   ╠-----╬-----╬-----╬-----╬-----╬-----╬-----╬-----╣
 1 ║  w  ║     ║  w  ║     ║  w  ║     ║  w  ║     ║
   ╚═════╩═════╩═════╩═════╩═════╩═════╩═════╩═════╝
      a     b     c     d     e     f     g     h
*/

// Border templates:
private val upperLine = "   ╔═════" + "╦═════".repeat(BOARD_DIM - 1) + "╗"
private val middleLine = "   ╠-----" + "╬-----".repeat(BOARD_DIM - 1) + "╣"
private val boardMiddleLine = "   ╠═════" + "╬═════".repeat(BOARD_DIM - 1) + "╣"
private val lowerLine = "   ╚═════" + "╩═════".repeat(BOARD_DIM - 1) + "╝"

/**
 * Prints a boardtype specific message to the stdout, along with a board for the
 * [local_player]
 * @param local_player Player assigned to a game session.
 */
fun Board.print(local_player: Player) = when(this) {
    is BoardDraw -> {
        println("\nGame ended in a draw since there were $MAX_MOVES_WITHOUT_CAPTURE" +
            " moves without a single capture")
    }
    is BoardWin -> {
        printLayout(local_player, winner)
        println("\nCongratulations! Player $winner has won the game")
    }
    is BoardRun -> {
        printLayout(local_player, turn)
    }
}

/**
 * Prints the current board layout to the stdout.
 */
fun Board.printLayout(local_player: Player, turn: Player) {
    require(BOARD_DIM < 100) { "Board dimensions bigger than 100 affect board printing "}
    println(upperLine)
    Square.values.forEach { sqr ->
        if (sqr.column.index == 0) // First print of the row
        if (sqr.row.number <= 9) print(" ${sqr.row} ║")
            else print("${sqr.row} ║")
        if (sqr.black && moves[sqr] != null) print("  ${moves[sqr]}  ║") // Square with a checker
        else if (sqr.black) print ("  -  ║") // Empty black square
        else print("     ║") // White square
        if (sqr.column.index == BOARD_DIM - 1) { // Last column of the row
            when (sqr.row.index) {
                0 -> print("  Turn = $turn")
                1 -> print("  Player = $local_player")
            }
            println()
            // Next line:
            if (sqr.row.index == BOARD_DIM /2 - 1) println(boardMiddleLine)
            else if (!sqr.onFirstRow) println(middleLine)
        }
    }
    println(lowerLine)
        .also { print("    ") }
        .also { Column.values.forEach { print("  ${it.symbol}   ") } }
        .also { println() }
}