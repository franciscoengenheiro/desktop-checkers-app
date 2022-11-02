package isel.leic.tds.checkers.ui
import isel.leic.tds.checkers.model.*
import java.io.*
import kotlin.test.*

class TestUi {
    @Test fun `print Board`() {
        require(BOARD_DIM == 8) { "Board dimension has to be 8" }
        val sut = initialBoard()
        val out = redirectInOut {
            sut.print()
        }
        assertEquals(listOf(
            "   +---------------+  Turn = w",
            " 8 |  b   b   b   b|  Player = w",
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
private fun redirectInOut(vararg lines:String, test: ()->Unit): List<String> {
    // Save old method for input to return it later
    val oldInput = System.`in`
    // Set new input
    System.setIn(ByteArrayInputStream(lines.joinToString(System.lineSeparator()).toByteArray()))
    // Save old method for output to return it later
    val oldOutput = System.out
    // TODO() Since JVM is the platform ???
    val result = ByteArrayOutputStream()
    // Set new output
    System.setOut(PrintStream(result))
    // Run received function
    test()
    // Return old input method
    System.setIn(oldInput)
    // Return old output method
    System.setOut(oldOutput)
    // Convert output into several strings separated by \r\n (carriage return and line feed respectively)
    val out = result.toString().split(System.lineSeparator())
    // TODO(Why this code?)
    return if (out.size>1 && out.last().isEmpty()) out.dropLast(1) else out
}