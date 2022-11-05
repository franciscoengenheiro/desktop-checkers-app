package isel.leic.tds.checkers.model

import isel.leic.tds.checkers.squaresToListOf
import isel.leic.tds.checkers.validateSqr
import kotlin.test.*

/**
 * The Square type identifies a position on the board (Column and Row)
 * Squares are identified by one (or more) digits and one letter.
 * The top left is "8a" (if BOARD_DIM==8)
 */
class TestSquare {
    @Test fun `Dimensions limits`() {
        assert(BOARD_DIM in 2..26 step 2){ "BOARD_DIM must be a pair in 2..26" }
    }
    @Test fun `Create a black square and convert to string`() {
        assertTrue(BOARD_DIM>2)
        val square = Square(1.indexToRow(),2.indexToColumn())
        assertEquals("${BOARD_DIM-1}${'a'+2}", square.toString())
        assertTrue(square.black)
    }
    @Test fun `String to a white square and use index values`() {
        assertTrue(BOARD_DIM>3)
        val square = "3d".toSquareOrNull()
        assertNotNull(square)
        assertEquals(3, square.column.index)
        assertEquals(BOARD_DIM-3, square.row.index)
        assertFalse(square.black)
    }
    @Test fun `Invalid string to Square results null`() {
        assertNull("b3b".toSquareOrNull())
        assertNull("b3".toSquareOrNull())
        assertNull("3$".toSquareOrNull())
        assertNull("${BOARD_DIM+1}a".toSquareOrNull())
        assertNull("1${'a'+ BOARD_DIM}".toSquareOrNull())
    }
    @Test fun `All valid squares`() {
        val all = Square.values
        assertEquals(BOARD_DIM * BOARD_DIM, all.size)
        assertEquals("${BOARD_DIM}a", all.first().toString())
        assertEquals("1${'a'+ BOARD_DIM-1}", all.last().toString())
    }
    @Test fun `Identity of squares`() {
        val row = 3.toRowOrNull()
        assertNotNull(row)
        val col = 'c'.toColumnOrNull()
        assertNotNull(col)
        val square = "3c".toSquareOrNull()
        assertNotNull(square)
        val s1 = Square(row.index,col.index)
        val s2 = Square(row,col)
        assertSame(square,s1)
        assertSame(square,s2)
    }
    @Test fun `Get adjacent diagonal squares from a Square`() {
        require(BOARD_DIM == 8) { "Board dim should be 8 for this test" }
        // Middle of the board Square
        val sqr1 = "4d".toSquareOrNull()
        assertNotNull(sqr1)
        val adjacentDiagonalList1 = sqr1.adjacentDiagonalsList
        assertEquals(4, adjacentDiagonalList1.size)
        val list1 = squaresToListOf("5c", "5e", "3c", "3e")
        assertEquals(list1, adjacentDiagonalList1)

        // Left Border Square
        val sqr2 = "5a".toSquareOrNull()
        assertNotNull(sqr2)
        val adjacentDiagonalList2 = sqr2.adjacentDiagonalsList
        assertEquals(2, adjacentDiagonalList2.size)
        val list2 = squaresToListOf("6b", "4b")
        assertEquals(list2, adjacentDiagonalList2)

        // Upper Border Square
        val sqr3 = "8d".toSquareOrNull()
        assertNotNull(sqr3)
        val adjacentDiagonalList3 = sqr3.adjacentDiagonalsList
        assertEquals(2, adjacentDiagonalList3.size)
        val list3 = squaresToListOf("7c", "7e")
        assertEquals(list3, adjacentDiagonalList3)

        // Corner Square
        val sqr4 = "1a".toSquareOrNull()
        assertNotNull(sqr4)
        val adjacentDiagonalList4 = sqr4.adjacentDiagonalsList
        assertEquals(1, adjacentDiagonalList4.size)
        val list4 = squaresToListOf("2b")
        assertEquals(list4, adjacentDiagonalList4)
    }
    @Test fun `Get diagonal squares from a Square`() {
        require(BOARD_DIM == 8) { "Board dim should be 8 for this test" }
        // Middle of the board Square
        val sqr1 = "4d".toSquareOrNull()
        assertNotNull(sqr1)
        val digonalList1 = sqr1.diagonalsList
        assertEquals(13, digonalList1.size)
        val list1 = squaresToListOf("1a", "2b", "3c", "5e", "6f", "7g", "8h", "7a", "6b",
            "5c", "3e", "2f", "1g")
        // In order to do the following comparison without taking the elemental order into
        // account, both lists were converted to sets.
        assertEquals(list1.toSet(), digonalList1.toSet())

        // Left Border Square
        val sqr2 = "6a".toSquareOrNull()
        assertNotNull(sqr2)
        val digonalList2 = sqr2.diagonalsList
        assertEquals(7, digonalList2.size)
        val list2 = squaresToListOf("7b", "8c", "5b", "4c", "3d", "2e", "1f")
        assertEquals(list2.toSet(), digonalList2.toSet())

        // Upper Border Square
        val sqr3 = "8f".toSquareOrNull()
        assertNotNull(sqr3)
        val digonalList3 = sqr3.diagonalsList
        assertEquals(7, digonalList3.size)
        val list3 = squaresToListOf("7e", "6d", "5c", "4b", "3a", "7g", "6h")
        assertEquals(list3.toSet(), digonalList3.toSet())

        // Lower Border Square
        val sqr4 = "1g".toSquareOrNull()
        assertNotNull(sqr4)
        val digonalList4 = sqr4.diagonalsList
        assertEquals(7, digonalList4.size)
        val list4 = squaresToListOf("2f", "3e", "4d", "5c", "6b", "7a", "2h")
        assertEquals(list4.toSet(), digonalList4.toSet())
    }
    @Test fun `Retrieve a diagonal by type from a Square`() {
        require(BOARD_DIM == 8) { "Board dim should be 8 for this test" }
        val sqr = validateSqr("5d")
        val upperBackSlash = squaresToListOf("6c", "7b", "8a")
        assertEquals(upperBackSlash, sqr.upperBackSlash)
        val upperSlash = squaresToListOf("6e", "7f", "8g")
        assertEquals(upperSlash, sqr.upperSlash)
        val lowerBackSlash = squaresToListOf("4e", "3f", "2g", "1h")
        assertEquals(lowerBackSlash, sqr.lowerBackSlash)
        val lowerSlash = squaresToListOf("4c", "3b", "2a")
        assertEquals(lowerSlash, sqr.lowerSlash)
    }
    @Test fun `Assert if a Square is on lastRow or firstRow`() {
        require(BOARD_DIM == 8) { "Board dim should be 8 for this test" }
        val sqr1 = "4d".toSquareOrNull()
        assertNotNull(sqr1)
        assertFalse(sqr1.onFirstRow)
        assertFalse(sqr1.onLastRow)
        val sqr2 = "1c".toSquareOrNull()
        assertNotNull(sqr2)
        assertTrue(sqr2.onFirstRow)
        assertFalse(sqr2.onLastRow)
        val sqr3 = "8f".toSquareOrNull()
        assertNotNull(sqr3)
        assertFalse(sqr3.onFirstRow)
        assertTrue(sqr3.onLastRow)
        Square.values
            .filter { it.row.index == 0 }
            .forEach { assertTrue(it.onLastRow) }
        Square.values
            .filter { it.row.index == BOARD_DIM - 1 }
            .forEach { assertTrue(it.onFirstRow) }
    }
}