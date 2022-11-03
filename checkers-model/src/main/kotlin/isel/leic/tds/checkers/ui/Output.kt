package isel.leic.tds.checkers.ui

import isel.leic.tds.checkers.model.*

// Board print model, assuming BOARD_DIM = 8
/*
  +---------------+  Turn = w
8 |  b   b   b   b|  Player = w
7 |b   b   b   b  |
6 |  b   b   b   b|
5 |-   -   -   -  |
4 |  -   -   -   -|
3 |w   w   w   w  |
2 |  w   w   w   w|
1 |w   w   w   w  |
  +---------------+
   a b c d e f g h
*/

// if BOARD_DIM = 8: divLine = +---------------+
private val divLine = "   +" + "-".repeat(BOARD_DIM * 2 - 1) + "+"

// Function to the print the current board to stdout
fun Board.print() = when(this) {
    is BoardDraw -> { println("Game ended in a draw since there were $MAX_MOVES_WITHOUT_CAPTURE" +
            " valid moves without a single capture") }
    is BoardWin -> { println("Congratulations! Player $winner has won the game") }
    is BoardRun -> {
        require(BOARD_DIM < 100) { "Board dimensions bigger than 100 affect board printing "}
        print(divLine).also { println ("  Turn = $turn") }
        Square.values.forEach { sqr ->
            if (sqr.column.index == 0)
                if (sqr.row.number <= 9) print(" ${sqr.row} |")
                else print("${sqr.row} |")
            if (sqr.black && moves[sqr] != null) print("${moves[sqr]}")
            else if (sqr.black) print ("-")
            else
                when (sqr.column.index) {
                    !in 1..BOARD_DIM - 2 -> print("  ")
                    else -> print("   ")
                }
            if (sqr.column.index == BOARD_DIM - 1)
                if (sqr.row.index == 0) println("|  Player = w") // TODO("change w to local player")
                else println("|")
        }
        println(divLine)
            .also { print("   ") }
            .also { Column.values.forEach { print(" ${it.symbol}") } }
            .also { println() }
    }
}