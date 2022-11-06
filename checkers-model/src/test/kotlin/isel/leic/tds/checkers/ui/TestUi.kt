package isel.leic.tds.checkers.ui

import isel.leic.tds.checkers.model.*
import java.io.*
import kotlin.test.*

class TestUi {
    @Test fun `readCommand Play Square1 Square2`() {
        val out = redirectInOut("","    ", "play    3c    4d ") {
            val (name,args) = readCommand()
            assertEquals("PLAY", name)
            assertEquals(listOf("3c", "4d"), args)
        }
        assertEquals(listOf("> > > "), out)
    }
    @Test fun `readCommand Continue gameName Player`() {
        val out = redirectInOut("CONTINUE game1 b") {
            val (name,args) = readCommand()
            assertEquals("CONTINUE", name)
            assertEquals(listOf("game1", "b"), args)
        }
        assertEquals(listOf("> "), out)
    }
    @Test fun `Print Board`() {
        require(BOARD_DIM == 8) { "Board dimension has to be 8" }
        val sut = initialBoard()
        val game = Game("game1", Player.b, initialBoard())
        val out = redirectInOut {
            sut.print(game)
        }
        assertEquals(listOf(
            "   +---------------+  Turn = w",
            " 8 |  b   b   b   b|  Player = b",
            " 7 |b   b   b   b  |",
            " 6 |  b   b   b   b|",
            " 5 |-   -   -   -  |",
            " 4 |  -   -   -   -|",
            " 3 |w   w   w   w  |",
            " 2 |  w   w   w   w|",
            " 1 |w   w   w   w  |",
            "   +---------------+",
            "    a b c d e f g h",
        ), out)
    }
}

/**
 * Executes the [test] function with the input and output redirected.
 * @param lines Each line that will be read from the input.
 * @param test Code to test that reads from stdin and writes to stdout.
 * @return Lines written in the output.
 */
private fun redirectInOut(vararg lines:String, test: () -> Unit): List<String> {
    // Save old method for input to return it later
    val oldInput = System.`in`
    // Set new input
    System.setIn(ByteArrayInputStream(lines.joinToString(System.lineSeparator()).toByteArray()))
    // Save old method for output to return it later
    val oldOutput = System.out
    val result = ByteArrayOutputStream()
    // Set new output
    System.setOut(PrintStream(result))
    // Run received test function
    test()
    // Return old input method
    System.setIn(oldInput)
    // Return old output method
    System.setOut(oldOutput)
    // Convert output into several strings separated by the OS line separator
    val out = result.toString().split(System.lineSeparator())
    return if (out.size > 1 && out.last().isEmpty()) out.dropLast(1) else out
}